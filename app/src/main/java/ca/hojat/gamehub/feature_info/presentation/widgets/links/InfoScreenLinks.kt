package ca.hojat.gamehub.feature_info.presentation.widgets.links

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.hojat.gamehub.core.domain.entities.WebsiteCategory
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import com.google.accompanist.flowlayout.FlowRow
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.widgets.GameHubCard
import ca.hojat.gamehub.feature_info.presentation.widgets.utils.InfoScreenSection
import java.util.Locale

@Composable
fun InfoScreenLinks(
    links: List<InfoScreenLinkUiModel>,
    onLinkClicked: (InfoScreenLinkUiModel) -> Unit,
) {
    InfoScreenSection(title = stringResource(R.string.game_info_links_title)) { paddingValues ->
        FlowRow(
            modifier = Modifier.padding(paddingValues),
            mainAxisSpacing = GameHubTheme.spaces.spacing_2_0,
            crossAxisSpacing = GameHubTheme.spaces.spacing_3_0,
        ) {
            for (link in links) {
                Link(
                    link = link,
                    onLinkClicked = { onLinkClicked(link) },
                )
            }
        }
    }
}

@Composable
private fun Link(
    link: InfoScreenLinkUiModel,
    onLinkClicked: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
        GameHubCard(
            onClick = onLinkClicked,
            shape = GameHubTheme.shapes.small,
            backgroundColor = GameHubTheme.colors.primaryVariant,
            contentColor = GameHubTheme.colors.onSurface,
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = GameHubTheme.spaces.spacing_1_5)
                    .padding(
                        start = GameHubTheme.spaces.spacing_2_5,
                        end = GameHubTheme.spaces.spacing_3_0,
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(link.iconId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = link.text,
                    modifier = Modifier.padding(start = GameHubTheme.spaces.spacing_1_5),
                    style = GameHubTheme.typography.button,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InfoScreenLinksPreview() {
    val links = WebsiteCategory.values()
        .filterNot { it == WebsiteCategory.UNKNOWN }
        .mapIndexed { index, websiteCategory ->
            InfoScreenLinkUiModel(
                id = index,
                text = websiteCategory.name
                    .replace("_", " ")
                    .lowercase()
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    },
                iconId = R.drawable.web,
                url = "url$index",
            )
        }

    GameHubTheme {
        InfoScreenLinks(
            links = links,
            onLinkClicked = {},
        )
    }
}
