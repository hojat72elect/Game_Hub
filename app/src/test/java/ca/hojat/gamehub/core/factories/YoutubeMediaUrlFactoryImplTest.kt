package ca.hojat.gamehub.core.factories

import ca.hojat.gamehub.core.domain.entities.Video
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

private val VIDEO = Video(
    id = "id",
    name = "name",
)

class YoutubeMediaUrlFactoryImplTest {

    private lateinit var sut: YoutubeMediaUrlFactoryImpl

    @Before
    fun setup() {
        sut = YoutubeMediaUrlFactoryImpl()
    }

    @Test
    fun `Creates thumbnail image urls correctly`() {
        for (ytThumbnailSize in YoutubeThumbnailSize.values()) {
            assertThat(sut.createThumbnailUrl(VIDEO, ytThumbnailSize))
                .isEqualTo("https://img.youtube.com/vi/${VIDEO.id}/${ytThumbnailSize.rawSize}.jpg")
        }
    }

    @Test
    fun `Returns null when video id is blank while creating thumbnail image url`() {
        assertThat(
            sut.createThumbnailUrl(
                VIDEO.copy(id = "   "),
                YoutubeThumbnailSize.MEDIUM
            )
        ).isNull()
    }

    @Test
    fun `Creates video urls correctly`() {
        for (ytThumbnailSize in YoutubeThumbnailSize.values()) {
            assertThat(sut.createVideoUrl(VIDEO))
                .isEqualTo("https://youtu.be/${VIDEO.id}")
        }
    }

    @Test
    fun `Returns null when video id is blank while creating video url`() {
        assertThat(sut.createVideoUrl(VIDEO.copy(id = "   "))).isNull()
    }
}
