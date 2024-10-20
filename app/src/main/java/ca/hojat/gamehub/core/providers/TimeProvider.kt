package ca.hojat.gamehub.core.providers

import com.paulrybitskyi.hiltbinder.BindType
import java.time.LocalDateTime
import javax.inject.Inject

interface TimeProvider {
    fun getCurrentDateTime(): LocalDateTime
}

@BindType
class TimeProviderImpl @Inject constructor() : TimeProvider {

    override fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}
