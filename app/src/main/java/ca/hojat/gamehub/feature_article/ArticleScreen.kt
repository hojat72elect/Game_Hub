package ca.hojat.gamehub.feature_article

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.CommandsHandler
import ca.hojat.gamehub.common_ui.LocalTextSharer
import ca.hojat.gamehub.common_ui.RoutesHandler
import ca.hojat.gamehub.common_ui.base.events.Route
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import ca.hojat.gamehub.common_ui.widgets.toolbars.Toolbar
import ca.hojat.gamehub.feature_news.presentation.widgets.Image
import ca.hojat.gamehub.feature_news.presentation.widgets.Timestamp
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData

/**
 * The composable for showing a specific article with its body.
 */
@Composable
fun ArticleScreen(onRoute: (Route) -> Unit) {
    ArticleScreen(
        viewModel = hiltViewModel(),
        onRoute = onRoute,
    )
}

@Composable
private fun ArticleScreen(
    viewModel: ArticleViewModel,
    onRoute: (Route) -> Unit,
) {
    val textSharer = LocalTextSharer.current
    val context = LocalContext.current

    CommandsHandler(viewModel = viewModel) { command ->
        when (command) {
            is ArticleCommand.ShareText -> {
                textSharer.share(context, command.text)
            }
        }
    }
    RoutesHandler(viewModel = viewModel, onRoute = onRoute)
    ArticleScreen(
        uiState = viewModel.uiState.collectAsState().value,
        onBackPressed = viewModel::onBackPressed,
        onShareButtonClicked = viewModel::onShareButtonClicked,
    )
}

@Composable
fun ArticleScreen(
    uiState: ArticleUiState,
    onBackPressed: () -> Unit,
    onShareButtonClicked: () -> Unit,
) {
    val webViewState = rememberWebViewStateWithHTMLData(data = uiState.body)
    BackHandler(onBack = onBackPressed)

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Toolbar(
            title = "",
            contentPadding = WindowInsets.statusBars.only(WindowInsetsSides.Vertical + WindowInsetsSides.Horizontal)
                .asPaddingValues(),
            backButtonIcon = painterResource(R.drawable.arrow_left),
            firstButtonIcon = painterResource(R.drawable.share_variant),
            onBackButtonClick = onBackPressed,
            onFirstButtonClick = onShareButtonClicked
        )
    }) {
        Column(modifier = Modifier.padding(10.dp)) {

            if (uiState.imageUrl != null) {
                Image(
                    imageUrl = checkNotNull(uiState.imageUrl),
                    modifier = Modifier
                        .height(168.dp)
                        .padding(bottom = GameHubTheme.spaces.spacing_3_5)
                )
            }
            Text(
                text = uiState.title,
                modifier = Modifier.fillMaxWidth(),
                color = GameHubTheme.colors.onPrimary,
                style = GameHubTheme.typography.subtitle2,
            )
            Text(
                text = uiState.lede,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = GameHubTheme.spaces.spacing_1_5),
                style = GameHubTheme.typography.body2.copy(
                    lineHeight = TextUnit.Unspecified,
                ),
            )
            Timestamp(publicationDate = uiState.publicationDate)
            WebView(
                state = webViewState,
                Modifier
                    .wrapContentWidth(align = Alignment.Start)
                    .fillMaxHeight()
                    .padding(top = GameHubTheme.spaces.spacing_1_5),
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ArticleScreenPreview() {
    GameHubTheme {
        ArticleScreen(
            uiState = ArticleUiState(
                "https://www.hitc.com/static/uploads/2023/02/Destiny-2-Lightfall-release-date-downtime-countdown-pre-order-bonuses-300x172.jpg",
                "Destiny 2 Sets New Concurrent Player Record On Steam As Lightfall Launches",
                "More than 300,000 people played destiny 2 at the same time today.",
                "Feb 28, 2:07 p.m.",
                "",
                "The body goes here."
            ),
            onBackPressed = {},
            onShareButtonClicked = {},
        )
    }
}