package ca.hojat.gamehub.core.providers

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface CustomTabsProvider {
    fun getCustomTabsPackageName(): String?
    fun areCustomTabsSupported(): Boolean
}

@Singleton
@BindType
class CustomTabsProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CustomTabsProvider {

    private companion object {
        private const val STABLE_PACKAGE = "com.android.chrome"
        private const val BETA_PACKAGE = "com.chrome.beta"
        private const val DEV_PACKAGE = "com.chrome.dev"
        private const val LOCAL_PACKAGE = "com.google.android.apps.chrome"
    }

    private var customTabsPackageName: String? = null

    override fun getCustomTabsPackageName(): String? {
        if (customTabsPackageName != null) return checkNotNull(customTabsPackageName)

        val packageManager = context.packageManager
        val viewIntentResolveInfoList = getViewIntentResolveInfoList()
        val packagesSupportingCustomTabs = buildList {
            for (resolveInfo in viewIntentResolveInfoList) {
                val serviceIntent = composeServiceIntent(resolveInfo)

                if (packageManager.resolveService(serviceIntent, 0) != null) {
                    add(resolveInfo.activityInfo.packageName)
                }
            }
        }

        return findPackageNameToUse(packagesSupportingCustomTabs)
            .also { customTabsPackageName = it }
    }

    private fun getViewIntentResolveInfoList(): List<ResolveInfo> {
        val packageManager = context.packageManager
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))

        return packageManager.queryIntentActivities(viewIntent, 0)
    }

    private fun composeServiceIntent(resolveInfo: ResolveInfo): Intent {
        return Intent().apply {
            `package` = resolveInfo.activityInfo.packageName
        }
    }

    private fun findPackageNameToUse(packagesSupportingCustomTabs: List<String>): String? {
        return when {
            (packagesSupportingCustomTabs.size == 1) -> packagesSupportingCustomTabs[0]
            packagesSupportingCustomTabs.contains(STABLE_PACKAGE) -> STABLE_PACKAGE
            packagesSupportingCustomTabs.contains(BETA_PACKAGE) -> BETA_PACKAGE
            packagesSupportingCustomTabs.contains(DEV_PACKAGE) -> DEV_PACKAGE
            packagesSupportingCustomTabs.contains(LOCAL_PACKAGE) -> LOCAL_PACKAGE

            else -> null
        }
    }

    override fun areCustomTabsSupported(): Boolean {
        return (getCustomTabsPackageName() != null)
    }
}
