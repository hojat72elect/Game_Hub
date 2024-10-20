package ca.hojat.gamehub.feature_news.presentation.widgets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import ca.hojat.gamehub.common_ui.images.defaultImageRequest
import ca.hojat.gamehub.common_ui.images.secondaryImage
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import coil.compose.AsyncImage
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.widgets.GameHubCard

@Composable
fun NewsScreenItem(
    model: NewsItemUiModel,
    onClick: () -> Unit
) {
    GameHubCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(GameHubTheme.spaces.spacing_4_0)) {
            if (model.hasImageUrl) {
                Image(
                    imageUrl = checkNotNull(model.imageUrl),
                    modifier = Modifier
                        .height(168.dp)
                        .padding(bottom = GameHubTheme.spaces.spacing_3_5)
                )
            }

            Text(
                text = model.title,
                modifier = Modifier.fillMaxWidth(),
                color = GameHubTheme.colors.onPrimary,
                style = GameHubTheme.typography.subtitle2,
            )
            Text(
                text = model.lede,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = GameHubTheme.spaces.spacing_1_5),
                style = GameHubTheme.typography.body2.copy(
                    lineHeight = TextUnit.Unspecified,
                ),
            )
            Timestamp(publicationDate = model.publicationDate)
        }
    }
}

/**
 * A simple yet proficient composable, you give it a URL and
 * a modifier, it shows the photo.
 */
@Composable
fun Image(
    imageUrl: String,
    modifier: Modifier
) {
    GameHubCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        shape = GameHubTheme.shapes.medium,
        backgroundColor = Color.Transparent,
    ) {
        AsyncImage(
            model = defaultImageRequest(imageUrl) {
                secondaryImage(R.drawable.game_landscape_placeholder)
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * a nice and informative line for showing the date.
 */
@Composable
fun Timestamp(publicationDate: String) {
    Row(
        modifier = Modifier.padding(top = GameHubTheme.spaces.spacing_2_5),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.clock_outline_16dp),
            contentDescription = null,
            modifier = Modifier.padding(end = GameHubTheme.spaces.spacing_1_0),
        )
        Text(
            text = publicationDate,
            style = GameHubTheme.typography.caption,
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GamingNewsItemWithImagePreview() {
    GameHubTheme {
        NewsScreenItem(
            model = NewsItemUiModel(
                id = 1,
                imageUrl = "url",
                title = "Steam Concurrent Player Count Breaks Record Again, Tops 26 Million",
                body = "",
                lede = "However, the record for those actively in a game has not been broken yet.",
                publicationDate = "3 mins ago",
                siteDetailUrl = "url",
            ),
            onClick = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GamingNewsItemWithoutImagePreview() {
    GameHubTheme {
        NewsScreenItem(
            model = NewsItemUiModel(
                id = 1,
                imageUrl = null,
                title = "Steam Concurrent Player Count Breaks Record Again, Tops 26 Million",
                body = "",
                lede = "However, the record for those actively in a game has not been broken yet.",
                publicationDate = "3 mins ago",
                siteDetailUrl = "url",
            ),
            onClick = {}
        )
    }
}
