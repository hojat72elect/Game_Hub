package ca.hojat.gamehub.feature_search.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModel
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModelMapper
import ca.hojat.gamehub.common_ui.widgets.games.GamesUiState
import ca.hojat.gamehub.common_ui.widgets.games.mapToUiModels
import ca.hojat.gamehub.common_ui.base.BaseViewModel
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.core.extensions.onError
import ca.hojat.gamehub.core.extensions.resultOrError
import ca.hojat.gamehub.core.mappers.ErrorMapper
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.feature_search.domain.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.github.aakira.napier.Napier
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

private const val KEY_CURRENT_SEARCH_QUERY = "current_search_query"
private const val KEY_CONFIRMED_SEARCH_QUERY = "confirmed_search_query"

@HiltViewModel
class GamesSearchViewModel @Inject constructor(
    private val searchGamesUseCase: SearchGamesUseCase,
    private val uiModelMapper: GameUiModelMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val stringProvider: StringProvider,
    private val errorMapper: ErrorMapper,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private var hasMoreGamesToLoad = false

    private var currentSearchQuery by observeChanges { _, newQuery ->
        _uiState.update { it.copy(queryText = newQuery) }
        savedStateHandle[KEY_CURRENT_SEARCH_QUERY] = newQuery
    }

    private var confirmedSearchQuery by observeChanges { _, newQuery ->
        useCaseParams = useCaseParams.copy(searchQuery = newQuery)
        savedStateHandle[KEY_CONFIRMED_SEARCH_QUERY] = newQuery
    }

    private var pagination: Pagination
        set(value) {
            useCaseParams = useCaseParams.copy(pagination = value)
        }
        get() = useCaseParams.pagination

    private var useCaseParams = SearchGamesUseCase.Params(searchQuery = "")

    private var allLoadedGames = emptyList<GameUiModel>()

    private val _uiState = MutableStateFlow(createGamesSearchEmptyUiState())

    private val currentUiState: GamesSearchUiState
        get() = _uiState.value

    val uiState: StateFlow<GamesSearchUiState> = _uiState.asStateFlow()

    init {
        restoreState()
    }

    private fun restoreState() {
        if (savedStateHandle.contains(KEY_CURRENT_SEARCH_QUERY)) {
            currentSearchQuery = checkNotNull(savedStateHandle[KEY_CURRENT_SEARCH_QUERY])
        }

        val restoredConfirmedSearchQuery = savedStateHandle.get<String>(KEY_CONFIRMED_SEARCH_QUERY)

        if (restoredConfirmedSearchQuery == currentSearchQuery) {
            onSearchConfirmed(checkNotNull(savedStateHandle[KEY_CONFIRMED_SEARCH_QUERY]))
        }
    }

    private fun createGamesSearchEmptyUiState(): GamesSearchUiState {
        return GamesSearchUiState(
            queryText = confirmedSearchQuery,
            gamesUiState = createGamesEmptyUiState(),
        )
    }

    private fun createGamesEmptyUiState(): GamesUiState {
        return GamesUiState(
            isLoading = false,
            infoIconId = R.drawable.magnify,
            infoTitle = getUiStateInfoTitle(),
            games = emptyList(),
        )
    }

    private fun getUiStateInfoTitle(): String {
        return if (confirmedSearchQuery.isBlank()) {
            stringProvider.getString(R.string.games_search_info_title_default)
        } else {
            stringProvider.getString(
                R.string.games_search_info_title_empty,
                confirmedSearchQuery,
            )
        }
    }

    fun onToolbarBackButtonClicked() {
        route(GamesSearchRoute.Back)
    }

    fun onToolbarClearButtonClicked() {
        _uiState.update { it.copy(queryText = "") }
    }

    fun onQueryChanged(newQueryText: String) {
        currentSearchQuery = newQueryText
    }

    fun onSearchConfirmed(query: String) {
        if (query.isEmpty() || (confirmedSearchQuery == query)) return

        confirmedSearchQuery = query

        resetPagination()
        searchGames()
    }

    private fun resetPagination() {
        pagination = Pagination()
        allLoadedGames = emptyList()
    }

    private fun searchGames() = viewModelScope.launch {
        if (confirmedSearchQuery.isBlank()) {
            flowOf(createGamesEmptyUiState())
        } else {
            searchGamesUseCase.execute(useCaseParams)
                .resultOrError()
                .map(uiModelMapper::mapToUiModels)
                .flowOn(dispatcherProvider.computation)
                .map { games ->
                    currentUiState.gamesUiState.toSuccessState(
                        infoTitle = getUiStateInfoTitle(),
                        games = games,
                    )
                }
                .onError {
                    Napier.e(it) { "Failed to search games." }
                    dispatchCommand(GeneralCommand.ShowLongToast(errorMapper.mapToMessage(it)))
                    emit(createGamesEmptyUiState())
                }
                .onStart {
                    val games = if (isPerformingNewSearch()) {
                        emptyList()
                    } else {
                        currentUiState.gamesUiState.games
                    }

                    emit(currentUiState.gamesUiState.toLoadingState(games))
                }
                .map(::combineWithAlreadyLoadedGames)
        }
            .collect { emittedUiState ->
                configureNextLoad(emittedUiState)
                updateTotalGamesResult(emittedUiState)
                _uiState.update { it.copy(gamesUiState = emittedUiState) }
            }
    }

    private fun isPerformingNewSearch(): Boolean {
        return allLoadedGames.isEmpty()
    }

    private fun combineWithAlreadyLoadedGames(gamesUiState: GamesUiState): GamesUiState {
        if (!gamesUiState.hasLoadedNewGames() || allLoadedGames.isEmpty()) {
            return gamesUiState
        }

        val oldGames = allLoadedGames
        val newGames = gamesUiState.games

        // The reason for distinctBy is because IGDB API, unfortunately, returns sometimes
        // duplicate entries. This causes Compose to throw the following error:
        // - java.lang.IllegalArgumentException: Key 389 was already used. If you are using
        // - LazyColumn/Row please make sure you provide a unique key for each item.
        // We do indeed provide game's ID as key ID for each composable inside LazyColumn
        // to improve performance in some cases. To fix that crash, we are filtering
        // duplicate entries using .distinctBy extension.
        val totalGames = (oldGames + newGames).distinctBy(GameUiModel::id)

        return gamesUiState.toSuccessState(totalGames)
    }

    private fun GamesUiState.hasLoadedNewGames(): Boolean {
        return (!isLoading && games.isNotEmpty())
    }

    private fun configureNextLoad(gamesUiState: GamesUiState) {
        if (!gamesUiState.hasLoadedNewGames()) return

        val paginationLimit = useCaseParams.pagination.limit
        val gameCount = gamesUiState.games.size

        hasMoreGamesToLoad = ((gameCount % paginationLimit) == 0)
    }

    private fun updateTotalGamesResult(gamesUiState: GamesUiState) {
        if (!gamesUiState.hasLoadedNewGames()) return

        allLoadedGames = gamesUiState.games
    }

    fun onGameClicked(game: GameUiModel) {
        route(GamesSearchRoute.Info(game.id))
    }

    fun onBottomReached() {
        loadMoreGames()
    }

    private fun loadMoreGames() {
        if (!hasMoreGamesToLoad) return

        pagination = pagination.nextOffset()
        searchGames()
    }

    private fun observeChanges(
        onChange: (oldValue: String, newValue: String) -> Unit
    ): ReadWriteProperty<Any, String> {
        return Delegates.observable("") { _, oldValue, newValue ->
            onChange(oldValue, newValue)
        }
    }

}
