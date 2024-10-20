package ca.hojat.gamehub.core.providers

import android.content.Context
import android.text.format.DateFormat
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class TimeFormat {
    TWENTY_FOUR_HOURS, TWELVE_HOURS
}

interface TimeFormatProvider {
    fun getTimeFormat(): TimeFormat
}

@BindType
class TimeFormatProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TimeFormatProvider {

    override fun getTimeFormat(): TimeFormat {
        return if (DateFormat.is24HourFormat(context)) {
            TimeFormat.TWENTY_FOUR_HOURS
        } else {
            TimeFormat.TWELVE_HOURS
        }
    }
}
