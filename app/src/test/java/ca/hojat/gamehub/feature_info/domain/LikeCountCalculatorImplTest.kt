package ca.hojat.gamehub.feature_info.domain

import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAME
import org.junit.Before
import org.junit.Test

class LikeCountCalculatorImplTest {

    private lateinit var sut: LikeCountCalculatorImpl

    @Before
    fun setup() {
        sut = LikeCountCalculatorImpl()
    }

    @Test
    fun `Calculates like count properly when both follower count and hype count fields are not null`() {
        val game = DOMAIN_GAME.copy(followerCount = 20, hypeCount = 10)

        assertThat(sut.calculateLikeCount(game)).isEqualTo(30)
    }

    @Test
    fun `Calculates like count properly when follower count field is null`() {
        val game = DOMAIN_GAME.copy(followerCount = null, hypeCount = 50)

        assertThat(sut.calculateLikeCount(game)).isEqualTo(50)
    }

    @Test
    fun `Calculates like count properly when hype count field is null`() {
        val game = DOMAIN_GAME.copy(followerCount = 70, hypeCount = null)

        assertThat(sut.calculateLikeCount(game)).isEqualTo(70)
    }

    @Test
    fun `Calculates like count properly when both follower count and hype count fields are null`() {
        val game = DOMAIN_GAME.copy(followerCount = null, hypeCount = null)

        assertThat(sut.calculateLikeCount(game)).isEqualTo(0)
    }
}
