package ca.hojat.gamehub.core.providers

import android.content.Context
import ca.hojat.gamehub.core.extensions.isConnectedToNetwork
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface NetworkStateProvider {

    val isNetworkAvailable: Boolean
}

@BindType
class NetworkStateProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkStateProvider {

    override val isNetworkAvailable: Boolean
        get() = context.isConnectedToNetwork
}
