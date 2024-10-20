package ca.hojat.gamehub.core.factories

import ca.hojat.gamehub.core.domain.entities.Image
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

enum class IgdbImageSize(val rawSize: String) {
    SMALL_COVER("cover_small"), BIG_COVER("cover_big"),

    MEDIUM_SCREENSHOT("screenshot_med"), BIG_SCREENSHOT("screenshot_big"), HUGE_SCREENSHOT("screenshot_huge"),

    MEDIUM_LOGO("logo_med"),

    THUMBNAIL("thumb"), MICRO("micro"),

    HD("720p"), FULL_HD("1080p")
}

enum class IgdbImageExtension(val rawExtension: String) {
    JPG("jpg"), PNG("png")
}

interface IgdbImageUrlFactory {

    data class Config(
        val size: IgdbImageSize,
        val extension: IgdbImageExtension = IgdbImageExtension.JPG,
        val withRetinaSize: Boolean = false
    )

    fun createUrl(image: Image, config: Config): String?
}

fun IgdbImageUrlFactory.createUrls(
    images: List<Image>, config: IgdbImageUrlFactory.Config
): List<String> {
    if (images.isEmpty()) return emptyList()

    return images.mapNotNull { image ->
        createUrl(image, config)
    }
}

@BindType
class IgdbImageUrlFactoryImpl @Inject constructor() : IgdbImageUrlFactory {

    private companion object {
        private const val IMAGE_URL_TEMPLATE =
            "https://images.igdb.com/igdb/image/upload/t_%s/%s.%s"
        private const val IMAGE_TYPE_RETINA_EXTENSION = "_2x"
    }

    override fun createUrl(image: Image, config: IgdbImageUrlFactory.Config): String? {
        if (image.id.isBlank()) return null

        return String.format(
            IMAGE_URL_TEMPLATE, constructType(config), image.id, config.extension.rawExtension
        )
    }

    private fun constructType(config: IgdbImageUrlFactory.Config): String {
        return buildString {
            append(config.size.rawSize)

            if (config.withRetinaSize) {
                append(IMAGE_TYPE_RETINA_EXTENSION)
            }
        }
    }
}
