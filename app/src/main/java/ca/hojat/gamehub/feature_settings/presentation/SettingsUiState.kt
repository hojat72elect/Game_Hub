package ca.hojat.gamehub.feature_settings.presentation

import androidx.compose.runtime.Immutable
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState

@Immutable
data class SettingsUiState(
    val isLoading: Boolean,
    val sections: List<SettingsSectionUiModel>,
    val selectedThemeName: String?,
    val isThemePickerVisible: Boolean,
    val selectedLanguageName: String?,
    val isLanguagePickerVisible: Boolean
)

val SettingsUiState.finiteUiState: FiniteUiState
    get() = when {
        isInLoadingState -> FiniteUiState.LOADING
        isInSuccessState -> FiniteUiState.SUCCESS
        else -> error("Unknown settings UI state.")
    }

private val SettingsUiState.isInLoadingState: Boolean
    get() = (isLoading && sections.isEmpty())

private val SettingsUiState.isInSuccessState: Boolean
    get() = sections.isNotEmpty()

fun SettingsUiState.toLoadingState(): SettingsUiState {
    return copy(isLoading = true)
}

fun SettingsUiState.toSuccessState(
    sections: List<SettingsSectionUiModel>,
    selectedThemeName: String,
    selectedLanguageName: String
): SettingsUiState {
    return copy(
        isLoading = false,
        sections = sections,
        selectedThemeName = selectedThemeName,
        selectedLanguageName = selectedLanguageName
    )
}

@Immutable
data class SettingsSectionUiModel(
    val id: Int,
    val title: String,
    val items: List<SettingsSectionItemUiModel>,
)

@Immutable
data class SettingsSectionItemUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val isClickable: Boolean = true,
)

enum class SettingSection(val id: Int) {
    GENERAL(id = 1),
    ABOUT(id = 2),
}

enum class SettingItem(val id: Int) {
    THEME(id = 1),
    LANGUAGE(id = 2),
    SOURCE_CODE(id = 3),
    VERSION(id = 4),
    PRIVACY_POLICY(id = 5)
}
