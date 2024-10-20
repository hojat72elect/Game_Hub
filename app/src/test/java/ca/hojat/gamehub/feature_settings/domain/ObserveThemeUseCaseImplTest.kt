package ca.hojat.gamehub.feature_settings.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_settings.DOMAIN_SETTINGS
import ca.hojat.gamehub.core.domain.common.usecases.execute
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_settings.domain.usecases.ObserveSettingsUseCase
import ca.hojat.gamehub.feature_settings.domain.usecases.ObserveThemeUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ObserveThemeUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var observeSettingsUseCase: ObserveSettingsUseCase

    private lateinit var sut: ObserveThemeUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveThemeUseCaseImpl(
            observeSettingsUseCase = observeSettingsUseCase,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits theme of settings that is emitted by another use case`() {
        runTest {
            every { observeSettingsUseCase.execute() } returns flowOf(DOMAIN_SETTINGS)

            sut.execute().test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_SETTINGS.theme)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Emits theme once if multiple events contain the same theme`() {
        runTest {
            every { observeSettingsUseCase.execute() } returns flowOf(
                DOMAIN_SETTINGS,
                DOMAIN_SETTINGS
            )

            sut.execute().test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_SETTINGS.theme)
                awaitComplete()
            }
        }
    }
}
