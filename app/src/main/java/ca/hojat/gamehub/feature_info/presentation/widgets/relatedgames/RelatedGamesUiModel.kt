package ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames

import androidx.compose.runtime.Immutable

@Immutable
data class RelatedGamesUiModel(
    val type: RelatedGamesType,
    val title: String,
    val items: List<RelatedGameUiModel>,
) {

    val hasItems: Boolean
        get() = items.isNotEmpty()
}
