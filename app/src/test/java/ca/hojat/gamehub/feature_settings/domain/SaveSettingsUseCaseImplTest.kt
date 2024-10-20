package ca.hojat.gamehub.feature_settings.domain

import ca.hojat.gamehub.feature_settings.DOMAIN_SETTINGS
import ca.hojat.gamehub.feature_settings.domain.datastores.SettingsLocalDataSource
import ca.hojat.gamehub.feature_settings.domain.usecases.SaveSettingsUseCaseImpl
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SaveSettingsUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var settingsLocalDataSource: SettingsLocalDataSource
    private lateinit var sut: SaveSettingsUseCaseImpl

    @Before
    fun setup() {
        settingsLocalDataSource = FakeSettingsLocalDataSource()
        sut = SaveSettingsUseCaseImpl(
            localDataStore = settingsLocalDataSource,
        )
    }

    @Test
    fun `Saves settings into local data store`() {
        runTest {
            val settings = DOMAIN_SETTINGS

            sut.execute(settings)

            assertThat(settingsLocalDataSource.observeSettings().first()).isEqualTo(settings)
        }
    }

    private class FakeSettingsLocalDataSource : SettingsLocalDataSource {

        private var settings: DomainSettings? = null

        override suspend fun saveSettings(settings: DomainSettings) {
            this.settings = settings
        }

        override fun observeSettings(): Flow<DomainSettings> {
            return if (settings == null) emptyFlow() else flowOf(checkNotNull(settings))
        }
    }
}
