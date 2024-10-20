package ca.hojat.gamehub.feature_discovery.widgets

import ca.hojat.gamehub.common_ui.widgets.categorypreview.GamesCategoryPreviewItemUiModel


fun List<DiscoverScreenItemData>.mapToCategoryUiModels(): List<GamesCategoryPreviewItemUiModel> {
    return map {
        GamesCategoryPreviewItemUiModel(
            id = it.id,
            title = it.title,
            coverUrl = it.coverUrl,
        )
    }
}

fun GamesCategoryPreviewItemUiModel.mapToDiscoveryUiModel(): DiscoverScreenItemData {
    return DiscoverScreenItemData(
        id = id,
        title = title,
        coverUrl = coverUrl,
    )
}
