package ca.hojat.gamehub.feature_info.presentation.widgets.header.artworks

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.clickable
import ca.hojat.gamehub.common_ui.images.defaultImageRequest
import ca.hojat.gamehub.common_ui.images.secondaryImage
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


@Composable
fun Artworks(
    artworks: List<InfoScreenArtworkUiModel>,
    isScrollingEnabled: Boolean,
    modifier: Modifier,
    onArtworkChanged: (artworkIndex: Int) -> Unit,
    onArtworkClicked: ((artworkIndex: Int) -> Unit)? = null,
) {
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page -> onArtworkChanged(page) }
    }

    HorizontalPager(
        count = artworks.size,
        modifier = modifier,
        state = pagerState,
        key = { page -> artworks[page].id },
        userScrollEnabled = isScrollingEnabled,
    ) { page ->
        Artwork(
            artwork = artworks[page],
            onArtworkClicked = {
                if (onArtworkClicked != null) {
                    onArtworkClicked(page)
                }
            },
        )
    }
}

@Composable
private fun Artwork(
    artwork: InfoScreenArtworkUiModel,
    onArtworkClicked: () -> Unit,
) {
    val data = when (artwork) {
        is InfoScreenArtworkUiModel.DefaultImage -> R.drawable.game_background_placeholder
        is InfoScreenArtworkUiModel.UrlImage -> artwork.url
    }

    AsyncImage(
        model = defaultImageRequest(data) {
            secondaryImage(R.drawable.game_background_placeholder)
        },
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                onClick = onArtworkClicked,
            ),
        contentScale = ContentScale.Crop,
    )
}

@Preview(heightDp = 240)
@Preview(heightDp = 240, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ArtworksPreview() {
    GameHubTheme {
        Artworks(
            artworks = listOf(
                InfoScreenArtworkUiModel.DefaultImage,
            ),
            isScrollingEnabled = true,
            modifier = Modifier,
            onArtworkChanged = {},
            onArtworkClicked = {},
        )
    }
}
