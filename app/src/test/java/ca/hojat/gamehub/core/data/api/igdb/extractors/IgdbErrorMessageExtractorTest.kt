package ca.hojat.gamehub.core.data.api.igdb.extractors

import ca.hojat.gamehub.core.data.api.igdb.common.errorextractors.IgdbErrorMessageExtractor
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class IgdbErrorMessageExtractorTest {

    private lateinit var sut: IgdbErrorMessageExtractor

    @Before
    fun setup() {
        sut = IgdbErrorMessageExtractor(Json)
    }

    @Test
    fun `Extracts igdb error message successfully`() {
        val responseBody = """
            [
              {
                "title": "Syntax Error",
                "status": 400,
                "cause": "Missing `;` at end of query"
              }
            ]
        """.trimIndent()

        assertThat(sut.extract(responseBody)).isEqualTo("Syntax Error")
    }

    @Test
    fun `Throws exception when igdb response body is not json`() {
        assertThrows(IllegalStateException::class.java) {
            sut.extract("hello there")
        }
    }

    @Test
    fun `Throws exception when igdb response body does not have message field`() {
        val responseBody = """
            [
              {
                "cause": "Syntax Error",
                "status": 400
              }
            ]
        """.trimIndent()

        assertThrows(IllegalStateException::class.java) {
            sut.extract(responseBody)
        }
    }
}
