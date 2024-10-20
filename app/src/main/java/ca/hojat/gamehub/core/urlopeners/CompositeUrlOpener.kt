package ca.hojat.gamehub.core.urlopeners

import android.content.Context
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

@BindType
class CompositeUrlOpener @Inject constructor(
    private val urlOpeners: List<@JvmSuppressWildcards UrlOpener>
) : UrlOpener {

    /**
     * Any composable that wants to open a url will call this function.
     */
    override fun openUrl(url: String, context: Context): Boolean {
        return urlOpeners.any {
            it.openUrl(url, context)
        }
    }
}
