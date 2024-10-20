package ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames

import ca.hojat.gamehub.common_ui.widgets.categorypreview.GamesCategoryPreviewItemUiModel

fun List<RelatedGameUiModel>.mapToCategoryUiModels(): List<GamesCategoryPreviewItemUiModel> {
    return map {
        GamesCategoryPreviewItemUiModel(
            id = it.id,
            title = it.title,
            coverUrl = it.coverUrl
        )
    }
}

fun GamesCategoryPreviewItemUiModel.mapToInfoRelatedGameUiModel(): RelatedGameUiModel {
    return RelatedGameUiModel(
        id = id,
        title = title,
        coverUrl = coverUrl
    )
}
