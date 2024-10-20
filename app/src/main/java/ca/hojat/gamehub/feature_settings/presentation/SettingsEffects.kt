package ca.hojat.gamehub.feature_settings.presentation

import ca.hojat.gamehub.common_ui.base.events.Command

sealed class SettingsCommand : Command {
    data class OpenUrl(val url: String) : SettingsCommand()
}
