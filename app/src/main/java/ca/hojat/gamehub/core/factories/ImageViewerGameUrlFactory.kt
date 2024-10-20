package ca.hojat.gamehub.core.factories

import ca.hojat.gamehub.core.domain.entities.Game
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface ImageViewerGameUrlFactory {
    fun createCoverImageUrl(game: Game): String?
    fun createArtworkImageUrls(game: Game): List<String>
    fun createScreenshotImageUrls(game: Game): List<String>
}

@BindType
class ImageViewerGameUrlFactoryImpl @Inject constructor(
    private val igdbImageUrlFactory: IgdbImageUrlFactory
) : ImageViewerGameUrlFactory {

    private companion object {
        private val IMAGE_SIZE = IgdbImageSize.HD
    }

    override fun createCoverImageUrl(game: Game): String? {
        return game.cover?.let { cover ->
            igdbImageUrlFactory.createUrl(cover, IgdbImageUrlFactory.Config(IMAGE_SIZE))
        }
    }

    override fun createArtworkImageUrls(game: Game): List<String> {
        return igdbImageUrlFactory.createUrls(game.artworks, IgdbImageUrlFactory.Config(IMAGE_SIZE))
    }

    override fun createScreenshotImageUrls(game: Game): List<String> {
        return igdbImageUrlFactory.createUrls(
            game.screenshots, IgdbImageUrlFactory.Config(IMAGE_SIZE)
        )
    }
}
