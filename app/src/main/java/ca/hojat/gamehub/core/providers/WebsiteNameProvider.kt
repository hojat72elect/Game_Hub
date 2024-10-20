package ca.hojat.gamehub.core.providers

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.domain.entities.Website
import ca.hojat.gamehub.core.domain.entities.WebsiteCategory
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface WebsiteNameProvider {
    fun provideWebsiteName(website: Website): String
}

@BindType
class WebsiteNameProviderImpl @Inject constructor(
    private val stringProvider: StringProvider
) : WebsiteNameProvider {

    @Suppress("ComplexMethod")
    override fun provideWebsiteName(website: Website): String {
        return stringProvider.getString(
            when (website.category) {
                WebsiteCategory.UNKNOWN -> R.string.website_unknown
                WebsiteCategory.OFFICIAL -> R.string.website_official
                WebsiteCategory.WIKIA -> R.string.website_wikia
                WebsiteCategory.WIKIPEDIA -> R.string.website_wikipedia
                WebsiteCategory.FACEBOOK -> R.string.website_facebook
                WebsiteCategory.TWITTER -> R.string.website_twitter
                WebsiteCategory.TWITCH -> R.string.website_twitch
                WebsiteCategory.INSTAGRAM -> R.string.website_instagram
                WebsiteCategory.YOUTUBE -> R.string.website_youtube
                WebsiteCategory.APP_STORE -> R.string.website_app_store
                WebsiteCategory.GOOGLE_PLAY -> R.string.website_google_play
                WebsiteCategory.STEAM -> R.string.website_steam
                WebsiteCategory.SUBREDDIT -> R.string.website_subreddit
                WebsiteCategory.EPIC_GAMES -> R.string.website_epic_games
                WebsiteCategory.GOG -> R.string.website_gog
                WebsiteCategory.DISCORD -> R.string.website_discord
            }
        )
    }
}
