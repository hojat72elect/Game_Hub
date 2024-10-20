package ca.hojat.gamehub.feature_settings.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_settings.DOMAIN_SETTINGS
import ca.hojat.gamehub.core.domain.common.usecases.execute
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_settings.domain.datastores.SettingsLocalDataSource
import ca.hojat.gamehub.feature_settings.domain.usecases.ObserveSettingsUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ObserveSettingsUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var settingsLocalDataSource: SettingsLocalDataSource

    private lateinit var sut: ObserveSettingsUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveSettingsUseCaseImpl(
            localDataSource = settingsLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits settings from local data store`() {
        runTest {
            every { settingsLocalDataSource.observeSettings() } returns flowOf(DOMAIN_SETTINGS)

            sut.execute().test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_SETTINGS)
                awaitComplete()
            }
        }
    }
}
