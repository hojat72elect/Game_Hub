package ca.hojat.gamehub.feature_info.presentation.widgets.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ca.hojat.gamehub.common_ui.widgets.GameHubCard
import ca.hojat.gamehub.common_ui.theme.GameHubTheme

@Composable
fun InfoScreenSection(
    title: String,
    modifier: Modifier = Modifier,
    titleBottomPadding: Dp = GameHubTheme.spaces.spacing_2_5,
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    GameHubCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        val contentPadding = GameHubTheme.spaces.spacing_3_5

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = contentPadding),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = contentPadding)
                    .padding(bottom = titleBottomPadding),
                color = GameHubTheme.colors.onPrimary,
                style = GameHubTheme.typography.h6,
            )

            content(PaddingValues(horizontal = contentPadding))
        }
    }
}

@Composable
fun InfoScreenSectionWithInnerList(
    title: String,
    content: LazyListScope.() -> Unit,
) {
    InfoScreenSection(title = title) { paddingValues ->
        LazyRow(
            contentPadding = paddingValues,
            horizontalArrangement = Arrangement.spacedBy(GameHubTheme.spaces.spacing_1_5),
            content = content,
        )
    }
}
