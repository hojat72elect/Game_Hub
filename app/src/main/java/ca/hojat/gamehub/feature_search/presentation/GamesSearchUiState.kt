package ca.hojat.gamehub.feature_search.presentation

import androidx.compose.runtime.Immutable
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModel
import ca.hojat.gamehub.common_ui.widgets.games.GamesUiState

@Immutable
data class GamesSearchUiState(
    val queryText: String,
    val gamesUiState: GamesUiState,
)

fun GamesUiState.toLoadingState(games: List<GameUiModel>): GamesUiState {
    return copy(
        isLoading = true,
        games = games,
    )
}

fun GamesUiState.toSuccessState(
    infoTitle: String,
    games: List<GameUiModel>,
): GamesUiState {
    return copy(
        isLoading = false,
        infoTitle = infoTitle,
        games = games
    )
}
