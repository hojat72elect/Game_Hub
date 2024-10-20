package ca.hojat.gamehub.feature_news.data

import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_news.data.datastores.gamespot.ArticlePublicationDateMapper
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.format.DateTimeParseException

class ArticlePublicationDateMapperTest {

    private lateinit var sut: ArticlePublicationDateMapper

    @Before
    fun setup() {
        sut = ArticlePublicationDateMapper()
    }

    @Test
    fun `Maps date successfully`() {
        assertThat(sut.mapToTimestamp("2020-03-02 14:30:16")).isEqualTo(1583188216000L)
    }

    @Test
    fun `Throws exception when providing empty date`() {
        assertThrows(DateTimeParseException::class.java) {
            sut.mapToTimestamp("")
        }
    }

    @Test
    fun `Throws exception when providing blank date`() {
        assertThrows(DateTimeParseException::class.java) {
            sut.mapToTimestamp("   ")
        }
    }
}
