package ca.hojat.gamehub.feature_settings.domain.usecases

import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.common.usecases.ObservableUseCase
import ca.hojat.gamehub.core.domain.common.usecases.execute
import ca.hojat.gamehub.feature_settings.domain.entities.Language
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ObserveLanguageUseCase : ObservableUseCase<Unit, Language>

@Singleton
@BindType
class ObserveLanguageUseCaseImpl @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ObserveLanguageUseCase {

    override fun execute(params: Unit): Flow<Language> {
        return observeSettingsUseCase.execute()
            .map { settings -> settings.language }
            .distinctUntilChanged()
            .flowOn(dispatcherProvider.main)
    }
}