package ca.hojat.gamehub.core.data.auth.file

import ca.hojat.gamehub.core.data.DOMAIN_OAUTH_CREDENTIALS
import ca.hojat.gamehub.core.data.auth.datastores.file.AUTH_TOKEN_TTL_DEDUCTION
import ca.hojat.gamehub.core.data.auth.datastores.file.AuthExpiryTimeCalculator
import ca.hojat.gamehub.core.providers.TimestampProvider
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

private const val CURRENT_TIMESTAMP = 10_000L

class AuthExpiryTimeCalculatorTest {

    @MockK
    private lateinit var timestampProvider: TimestampProvider

    private lateinit var sut: AuthExpiryTimeCalculator

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = AuthExpiryTimeCalculator(timestampProvider)

        every { timestampProvider.getUnixTimestamp(any()) } returns CURRENT_TIMESTAMP
    }

    @Test
    fun `Calculates expiry time successfully`() {
        val credentials = DOMAIN_OAUTH_CREDENTIALS
        val expiryTime = sut.calculateExpiryTime(credentials)
        val expected =
            (CURRENT_TIMESTAMP + TimeUnit.SECONDS.toMillis(credentials.tokenTtl) - AUTH_TOKEN_TTL_DEDUCTION)

        assertThat(expiryTime).isEqualTo(expected)
    }
}
