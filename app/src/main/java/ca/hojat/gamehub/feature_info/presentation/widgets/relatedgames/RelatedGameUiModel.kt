package ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames

import androidx.compose.runtime.Immutable

@Immutable
data class RelatedGameUiModel(
    val id: Int,
    val title: String,
    val coverUrl: String?,
)
