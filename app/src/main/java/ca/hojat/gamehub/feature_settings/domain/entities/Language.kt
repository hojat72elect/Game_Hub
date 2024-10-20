package ca.hojat.gamehub.feature_settings.domain.entities

import ca.hojat.gamehub.R

enum class Language(val uiTextRes: Int) {
    ENGLISH(R.string.settings_item_language_option_english),
    PERSIAN(R.string.settings_item_language_option_persian),
}