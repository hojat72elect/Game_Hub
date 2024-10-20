package ca.hojat.gamehub.core

import ca.hojat.gamehub.core.extensions.toMillis
import ca.hojat.gamehub.core.providers.TimeFormat
import ca.hojat.gamehub.core.providers.TimeFormatProvider
import ca.hojat.gamehub.core.providers.TimeProvider
import ca.hojat.gamehub.core.formatters.ArticlePublicationDateFormatterImpl
import ca.hojat.gamehub.core.formatters.RelativeDateFormatter
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

private const val RELATIVE_DATE = "relative_date"

class ArticlePublicationDateFormatterImplTest {

    @MockK
    private lateinit var relativeDateFormatter: RelativeDateFormatter

    @MockK
    private lateinit var timeProvider: TimeProvider

    @MockK
    private lateinit var timeFormatProvider: TimeFormatProvider

    private lateinit var sut: ArticlePublicationDateFormatterImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ArticlePublicationDateFormatterImpl(
            relativeDateFormatter = relativeDateFormatter,
            timeProvider = timeProvider,
            timeFormatProvider = timeFormatProvider
        )

        every { relativeDateFormatter.formatRelativeDate(any()) } returns RELATIVE_DATE
    }

    @Test
    fun `Formats pub date in relative format`() {
        val currentTime =
            LocalDateTime.of(2021, Month.MARCH, 4, 1, 15) // March 4th, 2021 at 1:15 AM
        val timestamp = currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        every { timeProvider.getCurrentDateTime() } returns currentTime

        assertThat(sut.formatPublicationDate(timestamp)).isEqualTo(RELATIVE_DATE)
    }

    @Test
    fun `Formats pub date in absolute 24 hours format without year`() {
        val currentTime =
            LocalDateTime.of(2021, Month.MARCH, 4, 1, 15) // March 4th, 2021 at 1:15 AM
        val timestamp = currentTime.minusDays(2).toMillis()

        every { timeProvider.getCurrentDateTime() } returns currentTime
        every { timeFormatProvider.getTimeFormat() } returns TimeFormat.TWENTY_FOUR_HOURS

        assertThat(sut.formatPublicationDate(timestamp)).isEqualTo("Mar 2, 1:15")
    }

    @Test
    fun `Formats pub date in absolute 24 hours format with year`() {
        val currentTime =
            LocalDateTime.of(2021, Month.MARCH, 4, 1, 15) // March 4th, 2021 at 1:15 AM
        val timestamp = currentTime.minusYears(1).toMillis()

        every { timeProvider.getCurrentDateTime() } returns currentTime
        every { timeFormatProvider.getTimeFormat() } returns TimeFormat.TWENTY_FOUR_HOURS

        assertThat(sut.formatPublicationDate(timestamp)).isEqualTo("Mar 4, 2020, 1:15")
    }

    @Test
    fun `Formats pub date in absolute 12 hours format without year`() {
        val currentTime =
            LocalDateTime.of(2021, Month.MARCH, 4, 1, 15) // March 4th, 2021 at 1:15 AM
        val timestamp = currentTime.minusDays(2).toMillis()

        every { timeProvider.getCurrentDateTime() } returns currentTime
        every { timeFormatProvider.getTimeFormat() } returns TimeFormat.TWELVE_HOURS

        assertThat(sut.formatPublicationDate(timestamp)).isEqualTo("Mar 2, 1:15 AM")
    }

    @Test
    fun `Formats pub date in absolute 12 hours format with year`() {
        val currentTime =
            LocalDateTime.of(2021, Month.MARCH, 4, 1, 15) // March 4th, 2021 at 1:15 AM
        val timestamp = currentTime.minusYears(1).toMillis()

        every { timeProvider.getCurrentDateTime() } returns currentTime
        every { timeFormatProvider.getTimeFormat() } returns TimeFormat.TWELVE_HOURS

        assertThat(sut.formatPublicationDate(timestamp)).isEqualTo("Mar 4, 2020, 1:15 AM")
    }
}
