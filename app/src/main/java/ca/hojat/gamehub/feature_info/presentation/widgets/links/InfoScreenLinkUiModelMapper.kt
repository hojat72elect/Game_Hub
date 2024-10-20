package ca.hojat.gamehub.feature_info.presentation.widgets.links

import ca.hojat.gamehub.core.domain.entities.Website
import ca.hojat.gamehub.core.domain.entities.WebsiteCategory
import ca.hojat.gamehub.core.providers.WebsiteIconProvider
import ca.hojat.gamehub.core.providers.WebsiteNameProvider
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface InfoScreenLinkUiModelMapper {
    fun mapToUiModel(website: Website): InfoScreenLinkUiModel?
    fun mapToUiModels(websites: List<Website>): List<InfoScreenLinkUiModel>
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class InfoScreenLinkUiModelMapperImpl @Inject constructor(
    private val websiteNameProvider: WebsiteNameProvider,
    private val websiteIconProvider: WebsiteIconProvider,
) : InfoScreenLinkUiModelMapper {

    override fun mapToUiModel(website: Website): InfoScreenLinkUiModel? {
        if (website.category == WebsiteCategory.UNKNOWN) return null

        return InfoScreenLinkUiModel(
            id = website.id,
            text = websiteNameProvider.provideWebsiteName(website),
            iconId = websiteIconProvider.provideIconIdForWebsite(website),
            url = website.url,
        )
    }

    override fun mapToUiModels(websites: List<Website>): List<InfoScreenLinkUiModel> {
        if (websites.isEmpty()) return emptyList()

        return websites
            .sortedBy { it.category.orderPosition }
            .mapNotNull(::mapToUiModel)
    }

    @Suppress("MagicNumber")
    private val WebsiteCategory.orderPosition: Int
        get() = when (this) {
            WebsiteCategory.UNKNOWN -> -1
            WebsiteCategory.STEAM -> 0
            WebsiteCategory.GOG -> 1
            WebsiteCategory.EPIC_GAMES -> 2
            WebsiteCategory.GOOGLE_PLAY -> 3
            WebsiteCategory.APP_STORE -> 4
            WebsiteCategory.OFFICIAL -> 5
            WebsiteCategory.TWITTER -> 6
            WebsiteCategory.SUBREDDIT -> 7
            WebsiteCategory.YOUTUBE -> 8
            WebsiteCategory.TWITCH -> 9
            WebsiteCategory.INSTAGRAM -> 10
            WebsiteCategory.FACEBOOK -> 11
            WebsiteCategory.WIKIPEDIA -> 12
            WebsiteCategory.FANDOM -> 13
            WebsiteCategory.DISCORD -> 14
        }
}
