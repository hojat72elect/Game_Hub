package ca.hojat.gamehub.core.urlopeners

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UrlOpenersModule {

    /**
     * This function provides a list of all the url openers; they will be handed over to
     * [CompositeUrlOpener] to be used for opening any kind of Url.
     * Im this list of url openers, the priority is like this: 1- native app url openers
     * 2- custom tab url openers 3- browser on device.
     */
    @Provides
    fun provideUrlOpeners(
        @UrlOpenerKey(UrlOpenerKey.Type.NATIVE_APP) nativeAppUrlOpener: UrlOpener,
        @UrlOpenerKey(UrlOpenerKey.Type.CUSTOM_TAB) customTabUrlOpener: UrlOpener,
        @UrlOpenerKey(UrlOpenerKey.Type.BROWSER) browserUrlOpener: UrlOpener
    ): List<UrlOpener> {
        return listOf(nativeAppUrlOpener, customTabUrlOpener, browserUrlOpener)
    }
}
