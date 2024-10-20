package ca.hojat.gamehub.core.providers

import com.paulrybitskyi.hiltbinder.BindType
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface TimestampProvider {
    fun getUnixTimestamp(timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Long
}

@BindType
class TimestampProviderImpl @Inject constructor() : TimestampProvider {

    override fun getUnixTimestamp(timeUnit: TimeUnit): Long {
        return timeUnit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    }
}
