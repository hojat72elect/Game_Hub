package ca.hojat.gamehub.feature_info.presentation.widgets.links

import androidx.compose.runtime.Immutable

@Immutable
data class InfoScreenLinkUiModel(
    val id: Int,
    val text: String,
    val iconId: Int,
    val url: String,
)
