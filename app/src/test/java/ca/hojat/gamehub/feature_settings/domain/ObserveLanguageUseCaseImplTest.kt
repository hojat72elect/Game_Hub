package ca.hojat.gamehub.feature_settings.domain

import app.cash.turbine.test
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.domain.common.usecases.execute
import ca.hojat.gamehub.feature_settings.DOMAIN_SETTINGS
import ca.hojat.gamehub.feature_settings.domain.usecases.ObserveLanguageUseCaseImpl
import ca.hojat.gamehub.feature_settings.domain.usecases.ObserveSettingsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ObserveLanguageUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var observeSettingsUseCase: ObserveSettingsUseCase

    private lateinit var sut: ObserveLanguageUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveLanguageUseCaseImpl(
            observeSettingsUseCase = observeSettingsUseCase,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits the language of settings that is emitted by another use case`() {
        runTest {
            every { observeSettingsUseCase.execute() } returns flowOf(DOMAIN_SETTINGS)


            sut.execute().test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_SETTINGS.language)
                awaitComplete()
            }

        }
    }

    @Test
    fun `emits the language once if multiple events contain the same language`() {
        runTest {
            every { observeSettingsUseCase.execute() } returns flowOf(
                DOMAIN_SETTINGS,
                DOMAIN_SETTINGS
            )

            sut.execute().test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_SETTINGS.language)
                awaitComplete()
            }
        }
    }
}