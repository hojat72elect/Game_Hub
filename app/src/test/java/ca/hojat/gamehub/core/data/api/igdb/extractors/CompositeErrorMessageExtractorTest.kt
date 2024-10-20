package ca.hojat.gamehub.core.data.api.igdb.extractors

import ca.hojat.gamehub.core.data.api.igdb.common.errorextractors.CompositeErrorMessageExtractor
import ca.hojat.gamehub.core.data.api.igdb.common.errorextractors.IgdbErrorMessageExtractor
import ca.hojat.gamehub.core.data.api.igdb.common.errorextractors.TwitchErrorMessageExtractor
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

class CompositeErrorMessageExtractorTest {

    private lateinit var sut: CompositeErrorMessageExtractor

    @Before
    fun setup() {
        sut = CompositeErrorMessageExtractor(
            setOf(
                TwitchErrorMessageExtractor(Json),
                IgdbErrorMessageExtractor(Json)
            )
        )
    }

    @Test
    fun `Extracts twitch error message from composite extractor successfully`() {
        val responseBody = """
            {
                "status":403,
                "message": "invalid client secret"
            }
        """.trimIndent()

        assertThat(sut.extract(responseBody)).isEqualTo("invalid client secret")
    }

    @Test
    fun `Extracts igdb error message from composite extractor successfully`() {
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
    fun `Returns unknown error if composite extractor fails to extract message`() {
        val responseBody = """
            {
                "status":403,
                "error_message": "invalid client secret"
            }
        """.trimIndent()

        assertThat(sut.extract(responseBody)).isEqualTo("Unknown Error: $responseBody")
    }
}
