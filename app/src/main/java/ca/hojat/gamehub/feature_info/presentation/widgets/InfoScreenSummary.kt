package ca.hojat.gamehub.feature_info.presentation.widgets

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import ca.hojat.gamehub.R
import ca.hojat.gamehub.feature_info.presentation.widgets.utils.InfoScreenSection

private const val AnimationDuration = 300
private const val ContentMaxLines = 4

@Composable
fun InfoScreenSummary(summary: String) {
    var hasTextBeenLaidOut by rememberSaveable { mutableStateOf(false) }
    var collapsedHeight by rememberSaveable { mutableStateOf(0) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isExpandable by rememberSaveable { mutableStateOf(true) }
    val cardClickableModifier by remember {
        derivedStateOf {
            if (isExpandable || isExpanded) {
                Modifier.clickable(
                    onClick = { isExpanded = !isExpanded },
                )
            } else {
                Modifier
            }
        }
    }

    InfoScreenSection(
        title = stringResource(R.string.game_info_summary_title),
        modifier = cardClickableModifier,
    ) { paddingValues ->
        AnimatedContent(
            targetState = isExpanded,
            modifier = Modifier.padding(paddingValues),
            transitionSpec = {
                val isExpanding = !initialState && targetState

                if (isExpanding) {
                    expandVertically(
                        animationSpec = tween(AnimationDuration),
                        expandFrom = Alignment.Top,
                        initialHeight = { collapsedHeight },
                    ) with ExitTransition.None
                } else {
                    EnterTransition.None with shrinkVertically(
                        animationSpec = tween(AnimationDuration),
                        shrinkTowards = Alignment.Top,
                        targetHeight = { collapsedHeight },
                    )
                } using SizeTransform(
                    sizeAnimationSpec = { _, _ -> tween(AnimationDuration) },
                )
            },
        ) { isInExpandedState ->
            Text(
                text = summary,
                color = GameHubTheme.colors.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (isInExpandedState) Int.MAX_VALUE else ContentMaxLines,
                onTextLayout = { textLayoutResult ->
                    if (!hasTextBeenLaidOut) {
                        hasTextBeenLaidOut = true
                        collapsedHeight = textLayoutResult.size.height
                        isExpandable = textLayoutResult.didOverflowHeight
                    }
                },
                style = GameHubTheme.typography.body1,
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InfoScreenSummaryCollapsedPreview() {
    GameHubTheme {
        InfoScreenSummary(
            summary = "Elden Ring is an action-RPG open world game with RPG " +
                    "elements such as stats, weapons and spells.",
        )
    }
}
