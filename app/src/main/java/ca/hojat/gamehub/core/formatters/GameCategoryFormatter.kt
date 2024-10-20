package ca.hojat.gamehub.core.formatters

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.domain.entities.Category
import ca.hojat.gamehub.core.providers.StringProvider
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface GameCategoryFormatter {
    fun formatCategory(category: Category): String
}

@BindType
class GameCategoryFormatterImpl @Inject constructor(
    private val stringProvider: StringProvider
) : GameCategoryFormatter {

    override fun formatCategory(category: Category): String {
        return stringProvider.getString(
            when (category) {
                Category.UNKNOWN -> R.string.not_available_abbr
                Category.MAIN_GAME -> R.string.game_category_main
                Category.BUNDLE -> R.string.game_category_bundle
                Category.MOD -> R.string.game_category_mod
                Category.EPISODE -> R.string.game_category_episode
                Category.SEASON -> R.string.game_category_season

                Category.DLC, Category.EXPANSION, Category.STANDALONE_EXPANSION -> R.string.game_category_dlc
            }
        )
    }
}
