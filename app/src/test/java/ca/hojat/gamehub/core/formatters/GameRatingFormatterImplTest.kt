package ca.hojat.gamehub.core.formatters

import ca.hojat.gamehub.core.providers.StringProvider
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.R
import org.junit.Before
import org.junit.Test

class GameRatingFormatterImplTest {

    private lateinit var stringProvider: FakeStringProvider
    private lateinit var sut: GameRatingFormatterImpl

    @Before
    fun setup() {
        stringProvider = FakeStringProvider()
        sut = GameRatingFormatterImpl(stringProvider)
    }

    @Test
    fun `Returns not available string when rating is null`() {
        sut.formatRating(null)

        assertThat(stringProvider.isRatingNotAvailable).isTrue()
    }

    @Test
    fun `Returns properly formatted string without coercing rating`() {
        val rating = 50.0

        sut.formatRating(rating)

        assertThat(stringProvider.isRatingAvailable).isTrue()
        assertThat(stringProvider.rating).isEqualTo(rating.toInt())
    }

    @Test
    fun `Returns properly formatted string with rating coerced to min rating`() {
        val rating = -50.0

        sut.formatRating(rating)

        assertThat(stringProvider.isRatingAvailable).isTrue()
        assertThat(stringProvider.rating).isEqualTo(0)
    }

    @Test
    fun `Returns properly formatted string with rating coerced to max rating`() {
        val rating = 150.0

        sut.formatRating(rating)

        assertThat(stringProvider.isRatingAvailable).isTrue()
        assertThat(stringProvider.rating).isEqualTo(100)
    }

    private class FakeStringProvider : StringProvider {

        var isRatingAvailable = false
        var isRatingNotAvailable = false

        var rating = 0

        override fun getString(id: Int, vararg args: Any): String {
            isRatingAvailable = (id == R.string.rating_template)
            isRatingNotAvailable = (id == R.string.not_available_abbr)

            if (args.isNotEmpty()) rating = (args.first() as Int)

            return ""
        }

        override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String {
            return ""
        }
    }
}
