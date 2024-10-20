package ca.hojat.gamehub.feature_settings.presentation

import app.cash.turbine.test
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState
import ca.hojat.gamehub.feature_settings.DOMAIN_SETTINGS
import ca.hojat.gamehub.feature_settings.domain.entities.Settings
import ca.hojat.gamehub.feature_settings.domain.entities.Theme
import ca.hojat.gamehub.feature_settings.domain.usecases.ObserveSettingsUseCase
import ca.hojat.gamehub.feature_settings.domain.usecases.SaveSettingsUseCase
import ca.hojat.gamehub.core.domain.common.usecases.execute
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.feature_settings.presentation.SettingsViewModel.Companion.PRIVACY_POLICY_LINK
import ca.hojat.gamehub.feature_settings.presentation.SettingsViewModel.Companion.SOURCE_CODE_LINK
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val saveSettingsUseCase = mockk<SaveSettingsUseCase>(relaxed = true)
    private val observeSettingsUseCase = mockk<ObserveSettingsUseCase>(relaxed = true)

    private val sut by lazy {
        SettingsViewModel(
            saveSettingsUseCase = saveSettingsUseCase,
            observeSettingsUseCase = observeSettingsUseCase,
            uiModelMapper = FakeSettingsUiModelMapper(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits correct ui states when initialization starts`() {
        runTest {
            every { observeSettingsUseCase.execute() } returns flowOf(DOMAIN_SETTINGS)

            sut.uiState.test {
                val loadingState = awaitItem()
                val resultState = awaitItem()

                assertThat(loadingState.finiteUiState).isEqualTo(FiniteUiState.LOADING)
                assertThat(resultState.finiteUiState).isEqualTo(FiniteUiState.SUCCESS)
                assertThat(resultState.sections).hasSize(FakeSettingsUiModelMapper.SECTION_ITEM_COUNT)
                assertThat(resultState.selectedThemeName).isEqualTo(DOMAIN_SETTINGS.theme.name)
            }
        }
    }

    @Test
    fun `Shows theme picker if theme setting is clicked`() {
        runTest {
            sut.uiState.test {
                sut.onSettingClicked(createSettingUiModel(SettingItem.THEME))

                val stateWithInvisiblePicker = awaitItem()
                val stateWithVisiblePicker = awaitItem()

                assertThat(stateWithInvisiblePicker.isThemePickerVisible).isFalse()
                assertThat(stateWithVisiblePicker.isThemePickerVisible).isTrue()
            }
        }
    }

    @Test
    fun `Hides theme picker when theme gets picked`() {
        runTest {
            sut.onSettingClicked(createSettingUiModel(SettingItem.THEME))

            sut.uiState.test {
                sut.onThemePicked(Theme.LIGHT)

                val stateWithVisiblePicker = awaitItem()
                val stateWithInvisiblePicker = awaitItem()

                assertThat(stateWithVisiblePicker.isThemePickerVisible).isTrue()
                assertThat(stateWithInvisiblePicker.isThemePickerVisible).isFalse()
            }
        }
    }

    @Test
    fun `Updates setting with new theme when theme gets picked`() {
        runTest {
            val defaultSettings = DOMAIN_SETTINGS.copy(theme = Theme.LIGHT)
            val newSettings = defaultSettings.copy(theme = Theme.DARK)

            every { observeSettingsUseCase.execute() } returns flowOf(defaultSettings)

            sut.onThemePicked(newSettings.theme)

            advanceUntilIdle()

            coVerify { saveSettingsUseCase.execute(newSettings) }
        }
    }

    @Test
    fun `Hides theme picker when picker gets dismissed`() {
        runTest {
            sut.onSettingClicked(createSettingUiModel(SettingItem.THEME))

            sut.uiState.test {
                sut.onThemePickerDismissed()

                val stateWithVisiblePicker = awaitItem()
                val stateWithInvisiblePicker = awaitItem()

                assertThat(stateWithVisiblePicker.isThemePickerVisible).isTrue()
                assertThat(stateWithInvisiblePicker.isThemePickerVisible).isFalse()
            }
        }
    }

    @Test
    fun `Opens source code link if source code setting is clicked`() {
        runTest {
            sut.commandFlow.test {
                sut.onSettingClicked(createSettingUiModel(SettingItem.SOURCE_CODE))

                val command = awaitItem()

                // Asserting that the command we got from this Flow was an "OpenUrl".
                assertThat(command).isInstanceOf(SettingsCommand.OpenUrl::class.java)
                // Asserting that the url that was supposed to be opened was source code of GameHub.
                assertThat((command as SettingsCommand.OpenUrl).url).isEqualTo(SOURCE_CODE_LINK)
            }
        }
    }

    @Test
    fun `Opens privacy policy link if privacy policy settings is clicked`() {
        runTest {
            sut.commandFlow.test {
                sut.onSettingClicked(createSettingUiModel(SettingItem.PRIVACY_POLICY))

                val command = awaitItem()

                assertThat(command).isInstanceOf(SettingsCommand.OpenUrl::class.java)
                assertThat((command as SettingsCommand.OpenUrl).url).isEqualTo(PRIVACY_POLICY_LINK)
            }
        }
    }


    private fun createSettingUiModel(setting: SettingItem): SettingsSectionItemUiModel {
        return SettingsSectionItemUiModel(
            id = setting.id,
            title = "title",
            description = "description",
        )
    }

    private class FakeSettingsUiModelMapper : SettingsUiModelMapper {

        companion object {
            const val SECTION_ITEM_COUNT = 3
        }

        override fun mapToUiModels(settings: Settings): List<SettingsSectionUiModel> {
            return buildList {
                repeat(SECTION_ITEM_COUNT) { index ->
                    add(
                        SettingsSectionUiModel(
                            id = index,
                            title = "title$index",
                            items = emptyList(),
                        )
                    )
                }
            }
        }
    }
}
