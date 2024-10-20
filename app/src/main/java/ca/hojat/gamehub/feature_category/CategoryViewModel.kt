package ca.hojat.gamehub.feature_category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.hojat.gamehub.common_ui.base.BaseViewModel
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.common_ui.di.TransitionAnimationDuration
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.games.common.ObserveUseCaseParams
import ca.hojat.gamehub.core.domain.games.common.RefreshUseCaseParams
import ca.hojat.gamehub.core.extensions.onError
import ca.hojat.gamehub.core.extensions.resultOrError
import ca.hojat.gamehub.core.mappers.ErrorMapper
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.feature_category.widgets.CategoryItemModelMapper
import ca.hojat.gamehub.feature_category.widgets.CategoryUiModel
import ca.hojat.gamehub.feature_category.widgets.CategoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.github.aakira.napier.Napier
import javax.inject.Inject

private const val PARAM_CATEGORY = "category"

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringProvider: StringProvider,
    @TransitionAnimationDuration
    transitionAnimationDuration: Long,
    private val useCases: CategoryUseCases,
    private val uiModelMapper: CategoryItemModelMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val errorMapper: ErrorMapper
) : BaseViewModel() {

    private var isObservingItems = false
    private var isRefreshingItems = false
    private var hasMoreItemsToLoad = false

    private var observeUseCaseParams = ObserveUseCaseParams()
    private var refreshUseCaseParams = RefreshUseCaseParams()

    private val categoryType = CategoryType.valueOf(checkNotNull(savedStateHandle.get<String>(PARAM_CATEGORY)))
    private val categoryKeyType = categoryType.toKeyType()

    private var observingJob: Job? = null
    private var refreshingJob: Job? = null

    private val _uiState = MutableStateFlow(createEmptyUiState())

    private val currentUiState: CategoryUiState
        get() = _uiState.value

    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {

        _uiState.update {
            it.copy(title = stringProvider.getString(categoryType.titleId))
        }

        observeItems(resultEmissionDelay = transitionAnimationDuration)
        refreshItems(resultEmissionDelay = transitionAnimationDuration)
    }

    private fun createEmptyUiState(): CategoryUiState {
        return CategoryUiState(
            isLoading = false,
            title = "",
            items = emptyList(),
        )
    }

    private fun observeItems(resultEmissionDelay: Long = 0L) {
        if (isObservingItems) return

        observingJob = useCases.getObservableGamesUseCase(categoryKeyType)
            .execute(observeUseCaseParams)
            .map(uiModelMapper::mapToUiModels)
            .flowOn(dispatcherProvider.computation)
            .map { items -> currentUiState.toSuccessState(items) }
            .onError {
                Napier.e(it){"Failed to observe ${categoryType.name} items."}
                dispatchCommand(GeneralCommand.ShowLongToast(errorMapper.mapToMessage(it)))
                emit(currentUiState.toEmptyState())
            }
            .onStart {
                isObservingItems = true
                delay(resultEmissionDelay)
            }
            .onCompletion { isObservingItems = false }
            .onEach { emittedUiState ->
                configureNextLoad(emittedUiState)
                _uiState.update { emittedUiState }
            }
            .launchIn(viewModelScope)
    }

    private fun configureNextLoad(uiState: CategoryUiState) {
        if (!uiState.hasLoadedNewGames()) return

        val paginationLimit = observeUseCaseParams.pagination.limit
        val itemsCount = uiState.items.size

        hasMoreItemsToLoad = (paginationLimit == itemsCount)
    }

    private fun refreshItems(resultEmissionDelay: Long = 0L) {
        if (isRefreshingItems) return

        refreshingJob = useCases.getRefreshableGamesUseCase(categoryKeyType)
            .execute(refreshUseCaseParams)
            .resultOrError()
            .map { currentUiState }
            .onError {
                Napier.e(it) { "Failed to refresh ${categoryType.name} games." }
                dispatchCommand(GeneralCommand.ShowLongToast(errorMapper.mapToMessage(it)))
            }
            .onStart {
                isRefreshingItems = true
                emit(currentUiState.enableLoading())
                // Show loading state for some time since it can be too quick
                delay(resultEmissionDelay)
            }
            .onCompletion {
                isRefreshingItems = false
                // Delay disabling loading to avoid quick state changes like
                // empty, loading, empty, success
                delay(resultEmissionDelay)
                emit(currentUiState.disableLoading())
            }
            .onEach { emittedUiState -> _uiState.update { emittedUiState } }
            .launchIn(viewModelScope)
    }

    fun onToolbarLeftButtonClicked() {
        route(CategoryScreenRoute.Back)
    }

    fun onItemClicked(item: CategoryUiModel) {
        route(CategoryScreenRoute.Info(item.id))
    }

    fun onBottomReached() {
        loadMoreItems()
    }

    private fun loadMoreItems() {
        if (!hasMoreItemsToLoad) return

        viewModelScope.launch {
            fetchNextBatch()
            observeNewBatch()
        }
    }

    private suspend fun fetchNextBatch() {
        refreshUseCaseParams = refreshUseCaseParams.copy(
            pagination = refreshUseCaseParams.pagination.nextOffset()
        )

        refreshingJob?.cancelAndJoin()
        refreshItems()
        refreshingJob?.join()
    }

    private suspend fun observeNewBatch() {
        observeUseCaseParams = observeUseCaseParams.copy(
            pagination = observeUseCaseParams.pagination.nextLimit()
        )

        observingJob?.cancelAndJoin()
        observeItems()
    }
}
