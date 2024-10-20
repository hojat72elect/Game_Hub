package ca.hojat.gamehub.feature_discovery.widgets

import androidx.compose.runtime.Immutable

@Immutable
data class DiscoverScreenItemData(
    val id: Int,
    val title: String,
    val coverUrl: String?,
)
