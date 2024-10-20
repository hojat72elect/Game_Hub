package ca.hojat.gamehub.feature_category

import ca.hojat.gamehub.R
import ca.hojat.gamehub.feature_category.di.CategoryKey

enum class CategoryType {
    POPULAR,
    RECENTLY_RELEASED,
    COMING_SOON,
    MOST_ANTICIPATED;

    val titleId: Int
        get() = when (this) {
            POPULAR -> R.string.games_category_popular
            RECENTLY_RELEASED -> R.string.games_category_recently_released
            COMING_SOON -> R.string.games_category_coming_soon
            MOST_ANTICIPATED -> R.string.games_category_most_anticipated
        }

    fun toKeyType(): CategoryKey.Type {
        return when (this) {
            POPULAR -> CategoryKey.Type.POPULAR
            RECENTLY_RELEASED -> CategoryKey.Type.RECENTLY_RELEASED
            COMING_SOON -> CategoryKey.Type.COMING_SOON
            MOST_ANTICIPATED -> CategoryKey.Type.MOST_ANTICIPATED
        }
    }
}
