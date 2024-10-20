package ca.hojat.gamehub.feature_info.presentation.widgets.videos

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.core.factories.YoutubeMediaUrlFactory
import ca.hojat.gamehub.core.factories.YoutubeThumbnailSize
import ca.hojat.gamehub.core.domain.entities.Video
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

abstract class InfoScreenVideoUiModelMapper {
    abstract fun mapToUiModel(video: Video): InfoScreenVideoUiModel?

    fun mapToUiModels(
        videos: List<Video>,
    ): List<InfoScreenVideoUiModel> {
        if (videos.isEmpty()) return emptyList()

        return videos.mapNotNull(::mapToUiModel)
    }
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class InfoScreenVideoUiModelMapperImpl @Inject constructor(
    private val youtubeMediaUrlFactory: YoutubeMediaUrlFactory,
    private val stringProvider: StringProvider,
) : InfoScreenVideoUiModelMapper() {

    override fun mapToUiModel(video: Video): InfoScreenVideoUiModel? {
        val thumbnailUrl = youtubeMediaUrlFactory.createThumbnailUrl(
            video,
            YoutubeThumbnailSize.MEDIUM
        )
        val videoUrl = youtubeMediaUrlFactory.createVideoUrl(video)
        val videoTitle = video.name ?: stringProvider.getString(
            R.string.game_info_video_title_fallback,
        )

        if ((thumbnailUrl == null) && (videoUrl == null)) return null

        return InfoScreenVideoUiModel(
            id = video.id,
            thumbnailUrl = checkNotNull(thumbnailUrl),
            videoUrl = checkNotNull(videoUrl),
            title = videoTitle,
        )
    }
}
