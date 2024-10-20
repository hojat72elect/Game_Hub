package ca.hojat.gamehub.feature_info.presentation.widgets.header.artworks

import ca.hojat.gamehub.core.factories.IgdbImageSize
import ca.hojat.gamehub.core.factories.IgdbImageUrlFactory
import ca.hojat.gamehub.core.domain.entities.Image
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

abstract class InfoScreenArtworkUiModelMapper {
    abstract fun mapToUiModel(image: Image): InfoScreenArtworkUiModel

    fun mapToUiModels(images: List<Image>): List<InfoScreenArtworkUiModel> {
        if (images.isEmpty()) return listOf(InfoScreenArtworkUiModel.DefaultImage)

        return images.map(::mapToUiModel)
            .filterIsInstance<InfoScreenArtworkUiModel.UrlImage>()
    }
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class InfoScreenArtworkUiModelMapperImpl @Inject constructor(
    private val igdbImageUrlFactory: IgdbImageUrlFactory,
) : InfoScreenArtworkUiModelMapper() {

    override fun mapToUiModel(image: Image): InfoScreenArtworkUiModel {
        return igdbImageUrlFactory.createUrl(
            image = image,
            config = IgdbImageUrlFactory.Config(IgdbImageSize.BIG_SCREENSHOT),
        )
            ?.let { url -> InfoScreenArtworkUiModel.UrlImage(id = image.id, url = url) }
            ?: InfoScreenArtworkUiModel.DefaultImage
    }
}
