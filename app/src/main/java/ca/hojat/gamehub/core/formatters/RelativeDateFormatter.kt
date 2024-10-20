package ca.hojat.gamehub.core.formatters

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.core.providers.TimeProvider
import com.paulrybitskyi.hiltbinder.BindType
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

interface RelativeDateFormatter {
    fun formatRelativeDate(dateTime: LocalDateTime): String
}

@BindType
class RelativeDateFormatterImpl @Inject constructor(
    private val timeProvider: TimeProvider, private val stringProvider: StringProvider
) : RelativeDateFormatter {

    override fun formatRelativeDate(dateTime: LocalDateTime): String {
        val currentDateTime = timeProvider.getCurrentDateTime()
        val isDateTimeInFuture = currentDateTime.isBefore(dateTime)

        return if (isDateTimeInFuture) {
            formatFutureDate(dateTime, currentDateTime)
        } else {
            formatPastDate(dateTime, currentDateTime)
        }
    }

    private fun formatFutureDate(dateTime: LocalDateTime, currentDateTime: LocalDateTime): String {
        val yearCount = ChronoUnit.YEARS.between(currentDateTime, dateTime).toInt()
        if (yearCount > 0L) return getQuantityString(
            R.plurals.future_relative_timestamp_year, yearCount
        )

        val monthCount = ChronoUnit.MONTHS.between(currentDateTime, dateTime).toInt()
        if (monthCount > 0L) return getQuantityString(
            R.plurals.future_relative_timestamp_month, monthCount
        )

        val dayCount = ChronoUnit.DAYS.between(currentDateTime, dateTime).toInt()
        if (dayCount > 0L) return getQuantityString(
            R.plurals.future_relative_timestamp_day, dayCount
        )

        val hourCount = ChronoUnit.HOURS.between(currentDateTime, dateTime).toInt()
        if (hourCount > 0L) return getQuantityString(
            R.plurals.future_relative_timestamp_hour, hourCount
        )

        val minuteCount = ChronoUnit.MINUTES.between(currentDateTime, dateTime).toInt()
        if (minuteCount > 0L) return getQuantityString(
            R.plurals.future_relative_timestamp_minute, minuteCount
        )

        val secondCount = ChronoUnit.SECONDS.between(currentDateTime, dateTime).toInt()
        if (secondCount > 0L) return getQuantityString(
            R.plurals.future_relative_timestamp_second, secondCount
        )

        throw IllegalStateException("Could not format the future date $dateTime in a relative way.")
    }

    private fun formatPastDate(dateTime: LocalDateTime, currentDateTime: LocalDateTime): String {
        val yearCount = ChronoUnit.YEARS.between(dateTime, currentDateTime).toInt()
        if (yearCount > 0L) return getQuantityString(
            R.plurals.past_relative_timestamp_year, yearCount
        )

        val monthCount = ChronoUnit.MONTHS.between(dateTime, currentDateTime).toInt()
        if (monthCount > 0L) return getQuantityString(
            R.plurals.past_relative_timestamp_month, monthCount
        )

        val dayCount = ChronoUnit.DAYS.between(dateTime, currentDateTime).toInt()
        if (dayCount > 0L) return getQuantityString(R.plurals.past_relative_timestamp_day, dayCount)

        val hourCount = ChronoUnit.HOURS.between(dateTime, currentDateTime).toInt()
        if (hourCount > 0L) return getQuantityString(
            R.plurals.past_relative_timestamp_hour, hourCount
        )

        val minuteCount = ChronoUnit.MINUTES.between(dateTime, currentDateTime).toInt()
        if (minuteCount > 0L) return getQuantityString(
            R.plurals.past_relative_timestamp_minute, minuteCount
        )

        val secondCount = ChronoUnit.SECONDS.between(dateTime, currentDateTime).toInt()
        if (secondCount > 0L) return getQuantityString(
            R.plurals.past_relative_timestamp_second, secondCount
        )

        throw IllegalStateException("Could not format the past date $dateTime in a relative way.")
    }

    private fun getQuantityString(id: Int, quantity: Int): String {
        return stringProvider.getQuantityString(id, quantity, quantity)
    }
}
