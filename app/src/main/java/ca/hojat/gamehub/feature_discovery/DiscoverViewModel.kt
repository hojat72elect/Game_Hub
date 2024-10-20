package ca.hojat.gamehub.feature_discovery

import androidx.lifecycle.viewModelScope
import ca.hojat.gamehub.common_ui.base.BaseViewModel
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.core.domain.games.common.ObserveUseCaseParams
import ca.hojat.gamehub.core.domain.games.common.RefreshUseCaseParams
import ca.hojat.gamehub.core.extensions.onError
import ca.hojat.gamehub.core.extensions.resultOrError
import ca.hojat.gamehub.core.mappers.ErrorMapper
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverItemModelMapper
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverScreen
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverScreenItemData
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverScreenUiModel
import ca.hojat.gamehub.feature_discovery.widgets.hideProgressBar
import ca.hojat.gamehub.feature_discovery.widgets.showProgressBar
import ca.hojat.gamehub.feature_discovery.widgets.toSuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import io.github.aakira.napier.Napier
import javax.inject.Inject

/**
 * The only View Model that controls [DiscoverScreen].
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val useCases: DiscoverUseCases,
    private val uiModelMapper: DiscoverItemModelMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val stringProvider: StringProvider,
    private val errorMapper: ErrorMapper
) : BaseViewModel() {

    private var isObservingItems = false
    private var isRefreshingItems = false

    private var observeUseCaseParams = ObserveUseCaseParams()
    private var refreshUseCaseParams = RefreshUseCaseParams()

    private val _items = MutableStateFlow<List<DiscoverScreenUiModel>>(listOf())

    private val currentItems: List<DiscoverScreenUiModel>
        get() = _items.value

    val items: StateFlow<List<DiscoverScreenUiModel>> = _items.asStateFlow()

    init {
        initDiscoveryItemsData()
        observeItems()
        refresh()
    }

    private fun initDiscoveryItemsData() {
        _items.update {
            DiscoverType.values().map { category ->
                DiscoverScreenUiModel(
                    id = category.id,
                    categoryName = category.name,
                    title = stringProvider.getString(category.titleId),
                    isProgressBarVisible = false,
                    items = emptyList(),
                )
            }
        }
    }

    private fun observeItems() {
        if (isObservingItems) return

        combine(
            flows = DiscoverType.values().map(::observeItems),
            transform = { it.toList() }
        )
            .map { listOfItems -> currentItems.toSuccessState(listOfItems) }
            .onError { Napier.e(it) { "Failed to observe games." } }
            .onStart { isObservingItems = true }
            .onCompletion { isObservingItems = false }
            .onEach { emittedItems -> _items.update { emittedItems } }
            .launchIn(viewModelScope)
    }

    private fun observeItems(category: DiscoverType): Flow<List<DiscoverScreenItemData>> {
        return useCases.getObservableGamesUseCase(category.toKeyType())
            .execute(observeUseCaseParams)
            .map(uiModelMapper::mapToUiModels)
            .flowOn(dispatcherProvider.computation)
    }

    /**
     * This function refreshes the whole screen items
     * (no matter what entity they are).
     */
    private fun refresh() {
        if (isRefreshingItems) return

        combine(
            flows = DiscoverType.values().map(::refreshGames),
            transform = { it.toList() }
        )
            .map { currentItems }
            .onError {
                Napier.e(it) { "Failed to refresh games." }
                dispatchCommand(GeneralCommand.ShowLongToast(errorMapper.mapToMessage(it)))
            }
            .onStart {
                isRefreshingItems = true
                emit(currentItems.showProgressBar())
            }
            .onCompletion {
                isRefreshingItems = false
                emit(currentItems.hideProgressBar())
            }
            .onEach { emittedItems -> _items.update { emittedItems } }
            .launchIn(viewModelScope)
    }

    /**
     * Returns a Flow<List<Game>> which technically refreshes the
     * games that are shown in the discover screen.
     */
    private fun refreshGames(category: DiscoverType): Flow<List<Game>> {
        return useCases.getRefreshableGamesUseCase(category.toKeyType())
            .execute(refreshUseCaseParams)
            .resultOrError()
    }

    fun onSearchButtonClicked() {
        route(DiscoverScreenRoute.Search)
    }

    fun onCategoryMoreButtonClicked(category: String) {
        route(DiscoverScreenRoute.Category(category))
    }

    fun onCategoryItemClicked(item: DiscoverScreenItemData) {
        route(DiscoverScreenRoute.Info(itemId = item.id))
    }

    fun onRefreshRequested() {
        refresh()
    }
}
