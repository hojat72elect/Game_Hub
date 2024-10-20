package ca.hojat.gamehub.feature_settings.domain.usecases

import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.common.usecases.ObservableUseCase
import ca.hojat.gamehub.feature_settings.domain.datastores.SettingsLocalDataSource
import ca.hojat.gamehub.feature_settings.domain.entities.Settings
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface ObserveSettingsUseCase : ObservableUseCase<Unit, Settings>

@Singleton
@BindType
class ObserveSettingsUseCaseImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : ObserveSettingsUseCase {

    override fun execute(params: Unit): Flow<Settings> {
        return localDataSource.observeSettings()
            .flowOn(dispatcherProvider.main)
    }
}
