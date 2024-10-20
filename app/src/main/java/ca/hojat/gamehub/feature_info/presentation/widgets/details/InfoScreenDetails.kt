package ca.hojat.gamehub.feature_info.presentation.widgets.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.theme.subtitle3
import ca.hojat.gamehub.feature_info.presentation.widgets.utils.InfoScreenSection

@Composable
fun InfoScreenDetails(details: InfoScreenDetailsUiModel) {
    InfoScreenSection(
        title = stringResource(R.string.game_info_details_title),
        titleBottomPadding = GameHubTheme.spaces.spacing_1_0,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (details.hasGenresText) {
                DetailsSectionRow(
                    title = stringResource(R.string.game_info_details_genres_title),
                    value = checkNotNull(details.genresText),
                )
            }

            if (details.hasPlatformsText) {
                DetailsSectionRow(
                    title = stringResource(R.string.game_info_details_platforms_title),
                    value = checkNotNull(details.platformsText),
                )
            }

            if (details.hasModesText) {
                DetailsSectionRow(
                    title = stringResource(R.string.game_info_details_modes_title),
                    value = checkNotNull(details.modesText),
                )
            }

            if (details.hasPlayerPerspectivesText) {
                DetailsSectionRow(
                    title = stringResource(R.string.game_info_details_player_perspectives_title),
                    value = checkNotNull(details.playerPerspectivesText),
                )
            }

            if (details.hasThemesText) {
                DetailsSectionRow(
                    title = stringResource(R.string.game_info_details_themes_title),
                    value = checkNotNull(details.themesText),
                )
            }
        }
    }
}

@Composable
private fun DetailsSectionRow(title: String, value: String) {
    Text(
        text = title,
        modifier = Modifier.padding(top = GameHubTheme.spaces.spacing_2_5),
        color = GameHubTheme.colors.onPrimary,
        style = GameHubTheme.typography.subtitle3,
    )
    Text(
        text = value,
        modifier = Modifier.padding(top = GameHubTheme.spaces.spacing_1_0),
        style = GameHubTheme.typography.body2,
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InfoScreenDetailsPreview() {
    GameHubTheme {
        InfoScreenDetails(
            details = InfoScreenDetailsUiModel(
                genresText = "Adventure • Shooter • Role-playing (RPG)",
                platformsText = "PC • PS4 • XONE • PS5 • Series X • Stadia",
                modesText = "Single Player • Multiplayer",
                playerPerspectivesText = "First person • Third person",
                themesText = "Action • Science Fiction • Horror • Survival",
            ),
        )
    }
}
