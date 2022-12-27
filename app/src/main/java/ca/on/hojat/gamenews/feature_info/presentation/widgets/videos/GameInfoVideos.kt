package ca.on.hojat.gamenews.feature_info.presentation.widgets.videos

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.on.hojat.gamenews.core.common_ui.images.defaultImageRequest
import ca.on.hojat.gamenews.core.common_ui.images.secondaryImage
import ca.on.hojat.gamenews.core.common_ui.theme.GameNewsTheme
import ca.on.hojat.gamenews.core.common_ui.theme.darkScrim
import ca.on.hojat.gamenews.core.common_ui.widgets.GameNewsCard
import coil.compose.AsyncImage
import ca.on.hojat.gamenews.R
import ca.on.hojat.gamenews.feature_info.presentation.widgets.utils.GameInfoSectionWithInnerList

@Composable
internal fun GameInfoVideos(
    videos: List<GameInfoVideoUiModel>,
    onVideClicked: (GameInfoVideoUiModel) -> Unit,
) {
    GameInfoSectionWithInnerList(title = stringResource(R.string.game_info_videos_title)) {
        items(items = videos, key = GameInfoVideoUiModel::id) { video ->
            Video(
                video = video,
                thumbnailHeight = 150.dp,
                modifier = Modifier.width(268.dp),
                onVideoClicked = { onVideClicked(video) },
            )
        }
    }
}

@Composable
private fun Video(
    video: GameInfoVideoUiModel,
    thumbnailHeight: Dp,
    modifier: Modifier,
    onVideoClicked: () -> Unit,
) {
    GameNewsCard(
        onClick = onVideoClicked,
        modifier = modifier,
        shape = GameNewsTheme.shapes.medium,
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(thumbnailHeight),
            ) {
                AsyncImage(
                    model = defaultImageRequest(video.thumbnailUrl) {
                        secondaryImage(R.drawable.game_landscape_placeholder)
                    },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(45.dp)
                        .border(
                            width = 2.dp,
                            color = LocalContentColor.current,
                            shape = CircleShape,
                        )
                        .background(
                            color = GameNewsTheme.colors.darkScrim,
                            shape = CircleShape,
                        )
                        .padding(GameNewsTheme.spaces.spacing_2_0),
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GameNewsTheme.colors.primaryVariant,
                contentColor = GameNewsTheme.colors.onSurface,
            ) {
                Text(
                    text = video.title,
                    modifier = Modifier.padding(GameNewsTheme.spaces.spacing_2_5),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = GameNewsTheme.typography.caption,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GameInfoVideosPreview() {
    GameNewsTheme {
        GameInfoVideos(
            videos = listOf(
                GameInfoVideoUiModel(
                    id = "1",
                    thumbnailUrl = "",
                    videoUrl = "",
                    title = "Announcement Trailer",
                ),
                GameInfoVideoUiModel(
                    id = "2",
                    thumbnailUrl = "",
                    videoUrl = "",
                    title = "Gameplay Trailer",
                ),
            ),
            onVideClicked = {},
        )
    }
}
