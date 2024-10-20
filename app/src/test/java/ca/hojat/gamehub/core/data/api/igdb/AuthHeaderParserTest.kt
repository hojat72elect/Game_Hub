package ca.hojat.gamehub.core.data.api.igdb

import ca.hojat.gamehub.core.data.api.igdb.auth.entities.ApiAuthorizationType
import ca.hojat.gamehub.core.data.api.igdb.auth.AuthHeaderParser
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class AuthHeaderParserTest {

    private companion object {
        const val TOKEN = "token"
    }

    private lateinit var sut: AuthHeaderParser

    @Before
    fun setup() {
        sut = AuthHeaderParser()
    }

    @Test
    fun `Returns null when header string is empty`() {
        assertThat(sut.parse("")).isNull()
    }

    @Test
    fun `Returns result with basic auth type`() {
        val expectedAuthType = ApiAuthorizationType.BASIC
        val actualResult = sut.parse("Basic $TOKEN")

        assertThat(actualResult).isNotNull()
        assertThat(actualResult!!.type).isEqualTo(expectedAuthType)
        assertThat(actualResult.token).isEqualTo(TOKEN)
    }

    @Test
    fun `Returns result with bearer auth type`() {
        val expectedAuthType = ApiAuthorizationType.BEARER
        val actualResult = sut.parse("Bearer $TOKEN")

        assertThat(actualResult).isNotNull()
        assertThat(actualResult!!.type).isEqualTo(expectedAuthType)
        assertThat(actualResult.token).isEqualTo(TOKEN)
    }
}
