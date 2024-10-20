package ca.hojat.gamehub.core.mappers

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.domain.entities.DomainException
import ca.hojat.gamehub.core.domain.entities.Error
import ca.hojat.gamehub.core.providers.StringProvider
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface ErrorMapper {
    fun mapToMessage(error: Throwable): String
}

@BindType
class ErrorMapperImpl @Inject constructor(
    private val stringProvider: StringProvider
) : ErrorMapper {

    /**
     * Just give it your [Throwable] and it will return a suitable message for it.
     */
    override fun mapToMessage(error: Throwable): String {
        if (error is DomainException) return error.toMessage()

        return stringProvider.getString(R.string.error_unknown_message)
    }

    private fun DomainException.toMessage(): String {
        return when (error) {
            is Error.ApiError -> error.toMessage()
            is Error.NotFound,
            is Error.Unknown -> stringProvider.getString(R.string.error_unknown_message)
        }
    }

    private fun Error.ApiError.toMessage(): String {
        return stringProvider.getString(
            when (this) {
                is Error.ApiError.NetworkError -> R.string.error_no_network_message
                is Error.ApiError.ServiceUnavailable -> R.string.error_api_server_message

                is Error.ApiError.ClientError,
                is Error.ApiError.Unknown -> R.string.error_api_unknown_message
            }
        )
    }
}
