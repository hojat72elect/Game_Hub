package ca.hojat.gamehub.core.urlopeners

import android.content.Context
import android.content.Intent
import android.net.Uri
import ca.hojat.gamehub.core.SdkInfo
import ca.hojat.gamehub.core.extensions.attachNewTaskFlagIfNeeded
import ca.hojat.gamehub.core.extensions.canUrlBeOpenedByNativeApp
import ca.hojat.gamehub.core.extensions.getNativeAppPackageForUrl
import com.paulrybitskyi.hiltbinder.BindType
import io.github.aakira.napier.Napier
import javax.inject.Inject

@BindType(withQualifier = true)
@UrlOpenerKey(UrlOpenerKey.Type.NATIVE_APP)
class NativeAppUrlOpener @Inject constructor() : UrlOpener {

    override fun openUrl(url: String, context: Context): Boolean {
        return if (SdkInfo.IS_AT_LEAST_11) {
            // API 30+
            openUrlInNewWay(url, context)
        } else {
            // before API 30
            openUrlInLegacyWay(url, context)
        }
    }

    private fun openUrlInNewWay(url: String, context: Context): Boolean {
        val intent = createIntent(url, context)
        intent.addFlags(Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER)

        return try {
            context.startActivity(intent)
            true
        } catch (throwable: Throwable) {
            Napier.e(throwable) { "Wasn't able to start a native app for showing URL" }
            false
        }
    }

    private fun openUrlInLegacyWay(url: String, context: Context): Boolean {
        if (!context.packageManager.canUrlBeOpenedByNativeApp(url)) return false

        val nativeAppPackage = context.packageManager.getNativeAppPackageForUrl(url)
        val intent = createIntent(url, context).apply {
            `package` = nativeAppPackage
        }

        context.startActivity(intent)

        return true
    }

    private fun createIntent(url: String, context: Context): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.attachNewTaskFlagIfNeeded(context)
        return intent
    }
}
