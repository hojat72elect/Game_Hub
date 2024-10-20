package ca.hojat.gamehub.feature_likes.presentation

import androidx.lifecycle.viewModelScope
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModel
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModelMapper
import ca.hojat.gamehub.common_ui.widgets.games.GamesUiState
import ca.hojat.gamehub.common_ui.widgets.games.mapToUiModels
import ca.hojat.gamehub.common_ui.base.BaseViewModel
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.games.common.ObserveUseCaseParams
import ca.hojat.gamehub.core.extensions.onError
import ca.hojat.gamehub.core.mappers.ErrorMapper
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.feature_likes.domain.ObserveLikedGamesUseCase
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

private const val SUBSEQUENT_EMISSION_DELAY = 500L

@HiltViewModel
class LikesViewModel @Inject constructor(
    private val observeLikedGamesUseCase: ObserveLikedGamesUseCase,
    private val uiModelMapper: GameUiModelMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val stringProvider: StringProvider,
    private val errorMapper: ErrorMapper
) : BaseViewModel() {

    private var isObservingGames = false
    private var hasMoreGamesToLoad = false

    private var observeUseCaseParams = ObserveUseCaseParams()

    private var gamesObservingJob: Job? = null

    private val _uiState = MutableStateFlow(createDefaultUiState())

    private val currentUiState: GamesUiState
        get() = _uiState.value

    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        observeGames()
    }

    private fun createDefaultUiState(): GamesUiState {
        return GamesUiState(
            isLoading = false,
            infoIconId = R.drawable.account_heart_outline,
            infoTitle = stringProvider.getString(R.string.liked_games_info_title),
            games = emptyList(),
        )
    }

    private fun observeGames() {
        if (isObservingGames) return

        gamesObservingJob = observeLikedGamesUseCase.execute(observeUseCaseParams)
            .map(uiModelMapper::mapToUiModels)
            .flowOn(dispatcherProvider.computation)
            .map { games -> currentUiState.toSuccessState(games) }
            .onError {
                Napier.e(it) { "Failed to load liked games." }
                dispatchCommand(GeneralCommand.ShowLongToast(errorMapper.mapToMessage(it)))
                emit(currentUiState.toEmptyState())
            }
            .onStart {
                isObservingGames = true
                emit(currentUiState.toLoadingState())

                // Delaying to give a sense of "loading" since progress indicators
                // do not have the time to fully show themselves
                if (isSubsequentEmission()) delay(SUBSEQUENT_EMISSION_DELAY)
            }
            .onCompletion { isObservingGames = false }
            .onEach { emittedUiState ->
                configureNextLoad(emittedUiState)
                _uiState.update { emittedUiState }
            }
            .launchIn(viewModelScope)
    }

    private fun isSubsequentEmission(): Boolean {
        return !observeUseCaseParams.pagination.hasDefaultLimit()
    }

    private fun configureNextLoad(uiState: GamesUiState) {
        if (!uiState.hasLoadedNewGames()) return

        val paginationLimit = observeUseCaseParams.pagination.limit
        val itemCount = uiState.games.size

        hasMoreGamesToLoad = (paginationLimit == itemCount)
    }

    private fun GamesUiState.hasLoadedNewGames(): Boolean {
        return (!isLoading && games.isNotEmpty())
    }

    fun onSearchButtonClicked() {
        route(LikesRoute.Search)
    }

    fun onGameClicked(game: GameUiModel) {
        route(LikesRoute.Info(game.id))
    }

    fun onBottomReached() {
        observeNewGamesBatch()
    }

    private fun observeNewGamesBatch() {
        if (!hasMoreGamesToLoad) return

        observeUseCaseParams = observeUseCaseParams.copy(
            pagination = observeUseCaseParams.pagination.nextLimit()
        )

        viewModelScope.launch {
            gamesObservingJob?.cancelAndJoin()
            observeGames()
        }
    }
}
