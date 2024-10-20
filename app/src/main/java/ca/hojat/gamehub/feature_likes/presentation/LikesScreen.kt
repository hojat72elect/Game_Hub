package ca.hojat.gamehub.feature_likes.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.CommandsHandler
import ca.hojat.gamehub.common_ui.NavBarColorHandler
import ca.hojat.gamehub.common_ui.RoutesHandler
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModel
import ca.hojat.gamehub.common_ui.widgets.games.Games
import ca.hojat.gamehub.common_ui.widgets.games.GamesUiState
import ca.hojat.gamehub.common_ui.widgets.toolbars.Toolbar
import ca.hojat.gamehub.common_ui.base.events.Route
import ca.hojat.gamehub.common_ui.theme.GameHubTheme

@Composable
fun LikesScreen(
    modifier: Modifier,
    onRoute: (Route) -> Unit,
) {
    LikesScreen(
        viewModel = hiltViewModel(),
        modifier = modifier,
        onRoute = onRoute,
    )
}

@Composable
private fun LikesScreen(
    viewModel: LikesViewModel,
    modifier: Modifier,
    onRoute: (Route) -> Unit,
) {
    NavBarColorHandler()
    CommandsHandler(viewModel = viewModel)
    RoutesHandler(viewModel = viewModel, onRoute = onRoute)
    LikesScreen(
        uiState = viewModel.uiState.collectAsState().value,
        onSearchButtonClicked = viewModel::onSearchButtonClicked,
        onGameClicked = viewModel::onGameClicked,
        onBottomReached = viewModel::onBottomReached,
        modifier = modifier,
    )
}

@Composable
private fun LikesScreen(
    uiState: GamesUiState,
    onSearchButtonClicked: () -> Unit,
    onGameClicked: (GameUiModel) -> Unit,
    onBottomReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                title = stringResource(R.string.liked_games_toolbar_title),
                contentPadding = WindowInsets.statusBars
                    .only(WindowInsetsSides.Vertical + WindowInsetsSides.Horizontal)
                    .asPaddingValues(),
                firstButtonIcon = painterResource(R.drawable.magnify),
                onFirstButtonClick = onSearchButtonClicked,
            )
        },
    ) { paddingValues ->
        Games(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onGameClicked = onGameClicked,
            onBottomReached = onBottomReached,
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotLikedAnyGames() {
    GameHubTheme {
        LikesScreen(
            uiState = GamesUiState(
                isLoading = false,
                infoIconId = R.drawable.account_heart_outline,
                infoTitle = stringResource(id = R.string.liked_games_info_title),
                games = emptyList(),
            ),
            onSearchButtonClicked = {},
            onGameClicked = {},
            onBottomReached = {},
        )
    }
}
