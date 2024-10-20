package ca.hojat.gamehub.feature_info.presentation.widgets.screenshots

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.hojat.gamehub.common_ui.images.defaultImageRequest
import ca.hojat.gamehub.common_ui.images.secondaryImage
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import coil.compose.AsyncImage
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.widgets.GameHubCard
import ca.hojat.gamehub.feature_info.presentation.widgets.utils.InfoScreenSectionWithInnerList

@Composable
fun InfoScreenShotSection(
    screenshots: List<InfoScreenShotUiModel>,
    onScreenshotClicked: (screenshotIndex: Int) -> Unit,
) {
    InfoScreenSectionWithInnerList(title = stringResource(R.string.game_info_screenshots_title)) {
        itemsIndexed(
            items = screenshots,
            key = { _, screenshot -> screenshot.id },
        ) { index, screenshot ->
            Screenshot(
                screenshot = screenshot,
                modifier = Modifier.size(width = 268.dp, height = 150.dp),
                onScreenshotClicked = { onScreenshotClicked(index) },
            )
        }
    }
}

@Composable
private fun Screenshot(
    screenshot: InfoScreenShotUiModel,
    modifier: Modifier,
    onScreenshotClicked: () -> Unit,
) {
    GameHubCard(
        onClick = onScreenshotClicked,
        modifier = modifier,
        shape = GameHubTheme.shapes.medium,
        backgroundColor = Color.Transparent,
    ) {
        AsyncImage(
            model = defaultImageRequest(screenshot.url) {
                secondaryImage(R.drawable.game_landscape_placeholder)
            },
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InfoScreenShotSectionPreview() {
    GameHubTheme {
        InfoScreenShotSection(
            screenshots = listOf(
                InfoScreenShotUiModel(
                    id = "1",
                    url = "",
                ),
                InfoScreenShotUiModel(
                    id = "2",
                    url = "",
                ),
                InfoScreenShotUiModel(
                    id = "3",
                    url = "",
                ),
            ),
            onScreenshotClicked = {},
        )
    }
}
