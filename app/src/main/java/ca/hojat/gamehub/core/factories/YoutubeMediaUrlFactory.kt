package ca.hojat.gamehub.core.factories

import ca.hojat.gamehub.core.domain.entities.Video
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

enum class YoutubeThumbnailSize(val rawSize: String) {
    DEFAULT("default"), // 120x90
    MEDIUM("mqdefault"), // 320x180
    HIGH("hqdefault"), // 480x360
    STANDARD_DEFINITION("sddefault"), // 640x480
    MAX("maxresdefault") // 1052x592
}

interface YoutubeMediaUrlFactory {
    fun createThumbnailUrl(video: Video, size: YoutubeThumbnailSize): String?
    fun createVideoUrl(video: Video): String?
}

@BindType
class YoutubeMediaUrlFactoryImpl @Inject constructor() : YoutubeMediaUrlFactory {

    private companion object {
        private const val THUMBNAIL_URL_TEMPLATE = "https://img.youtube.com/vi/%s/%s.jpg"
        private const val VIDEO_URL_TEMPLATE = "https://youtu.be/%s"
    }

    override fun createThumbnailUrl(video: Video, size: YoutubeThumbnailSize): String? {
        if (video.id.isBlank()) return null

        return String.format(THUMBNAIL_URL_TEMPLATE, video.id, size.rawSize)
    }

    override fun createVideoUrl(video: Video): String? {
        if (video.id.isBlank()) return null

        return String.format(VIDEO_URL_TEMPLATE, video.id)
    }
}
