package ca.hojat.gamehub.core.factories

import ca.hojat.gamehub.core.domain.entities.Image
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.Locale

private val IMAGE = Image(
    id = "id",
    width = 500,
    height = 1000,
)

class IgdbImageUrlFactoryImplTest {

    private lateinit var sut: IgdbImageUrlFactoryImpl

    @Before
    fun setup() {
        sut = IgdbImageUrlFactoryImpl()
    }

    @Test
    fun `Creates image urls without retina size correctly`() {
        for (imageExtension in IgdbImageExtension.values()) {
            for (igdbImageSize in IgdbImageSize.values()) {
                val config = IgdbImageUrlFactory.Config(
                    size = igdbImageSize,
                    extension = imageExtension
                )

                val expectedUrl = String.format(
                    Locale.US,
                    "https://images.igdb.com/igdb/image/upload/t_%s/%s.%s",
                    config.size.rawSize,
                    IMAGE.id,
                    config.extension.rawExtension
                )

                assertThat(sut.createUrl(IMAGE, config)).isEqualTo(expectedUrl)
            }
        }
    }

    @Test
    fun `Creates image urls with retina size correctly`() {
        for (imageExtension in IgdbImageExtension.values()) {
            for (igdbImageSize in IgdbImageSize.values()) {
                val config = IgdbImageUrlFactory.Config(
                    size = igdbImageSize,
                    extension = imageExtension,
                    withRetinaSize = true
                )

                val expectedUrl = String.format(
                    Locale.US,
                    "https://images.igdb.com/igdb/image/upload/t_%s/%s.%s",
                    (config.size.rawSize + "_2x"),
                    IMAGE.id,
                    config.extension.rawExtension
                )

                assertThat(sut.createUrl(IMAGE, config)).isEqualTo(expectedUrl)
            }
        }
    }

    @Test
    fun `Returns null when image id is blank while creating image url`() {
        val config = IgdbImageUrlFactory.Config(
            size = IgdbImageSize.BIG_COVER
        )

        assertThat(sut.createUrl(IMAGE.copy(id = "   "), config)).isNull()
    }
}
