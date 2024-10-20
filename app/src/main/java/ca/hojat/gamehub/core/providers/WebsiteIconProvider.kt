package ca.hojat.gamehub.core.providers

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.domain.entities.Website
import ca.hojat.gamehub.core.domain.entities.WebsiteCategory
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface WebsiteIconProvider {

    fun provideIconIdForWebsite(website: Website): Int
}

@BindType
class WebsiteIconProviderImpl @Inject constructor() : WebsiteIconProvider {

    override fun provideIconIdForWebsite(website: Website): Int {
        return when (website.category) {
            WebsiteCategory.WIKIPEDIA -> R.drawable.wikipedia
            WebsiteCategory.FACEBOOK -> R.drawable.facebook
            WebsiteCategory.TWITTER -> R.drawable.twitter
            WebsiteCategory.TWITCH -> R.drawable.twitch
            WebsiteCategory.INSTAGRAM -> R.drawable.instagram
            WebsiteCategory.YOUTUBE -> R.drawable.youtube
            WebsiteCategory.APP_STORE -> R.drawable.apple
            WebsiteCategory.GOOGLE_PLAY -> R.drawable.google_play
            WebsiteCategory.STEAM -> R.drawable.steam
            WebsiteCategory.SUBREDDIT -> R.drawable.reddit
            WebsiteCategory.GOG -> R.drawable.gog
            WebsiteCategory.DISCORD -> R.drawable.discord
            WebsiteCategory.UNKNOWN, WebsiteCategory.OFFICIAL, WebsiteCategory.FANDOM, WebsiteCategory.EPIC_GAMES -> R.drawable.web
        }
    }
}
