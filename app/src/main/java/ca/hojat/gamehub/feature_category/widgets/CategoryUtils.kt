package ca.hojat.gamehub.feature_category.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.hojat.gamehub.common_ui.theme.GameHubTheme

private const val GRID_SPAN_COUNT = 3
private const val ITEM_HEIGHT_TO_WIDTH_RATIO = 1.366f

/**
 * The config for Grid of items in [CategoryScreen]
 */
@Immutable
data class GridConfig(
    val spanCount: Int,
    val itemSpacingInPx: Float,
    val itemWidthInDp: Dp,
    val itemHeightInDp: Dp,
)

@Composable
fun rememberGridConfig(): GridConfig {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val gridItemSpacingInDp = GameHubTheme.spaces.spacing_0_5

    return remember(density, configuration) {
        val gridItemSpacingInPx = with(density) { gridItemSpacingInDp.toPx() }
        val screenWidthInPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }

        val horizontalTotalSpacing = (gridItemSpacingInPx * (GRID_SPAN_COUNT + 1))
        val availableScreenWidth = (screenWidthInPx - horizontalTotalSpacing)
        val itemWidth = (availableScreenWidth / GRID_SPAN_COUNT.toFloat())
        val itemWidthInDp = with(density) { itemWidth.toDp() }

        val itemHeight = (itemWidth * ITEM_HEIGHT_TO_WIDTH_RATIO)
        val itemHeightInDp = with(density) { itemHeight.toDp() }

        GridConfig(
            spanCount = GRID_SPAN_COUNT,
            itemSpacingInPx = gridItemSpacingInPx,
            itemWidthInDp = itemWidthInDp,
            itemHeightInDp = itemHeightInDp,
        )
    }
}
