package ca.hojat.gamehub.feature_discovery

import ca.hojat.gamehub.R
import ca.hojat.gamehub.feature_discovery.di.DiscoverKey
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverScreen

/**
 * Any of the different categories in the [DiscoverScreen].
 */
enum class DiscoverType(val id: Int) {
    POPULAR(id = 1),
    RECENTLY_RELEASED(id = 2),
    COMING_SOON(id = 3),
    MOST_ANTICIPATED(id = 4);

    val titleId: Int
        get() = when (this) {
            POPULAR -> R.string.games_category_popular
            RECENTLY_RELEASED -> R.string.games_category_recently_released
            COMING_SOON -> R.string.games_category_coming_soon
            MOST_ANTICIPATED -> R.string.games_category_most_anticipated

        }

    fun toKeyType(): DiscoverKey.Type {
        return when (this) {
            POPULAR -> DiscoverKey.Type.POPULAR
            RECENTLY_RELEASED -> DiscoverKey.Type.RECENTLY_RELEASED
            COMING_SOON -> DiscoverKey.Type.COMING_SOON
            MOST_ANTICIPATED -> DiscoverKey.Type.MOST_ANTICIPATED
        }
    }
}