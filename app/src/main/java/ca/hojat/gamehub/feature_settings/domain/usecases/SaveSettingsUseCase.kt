package ca.hojat.gamehub.feature_settings.domain.usecases

import ca.hojat.gamehub.core.domain.common.usecases.UseCase
import ca.hojat.gamehub.feature_settings.domain.datastores.SettingsLocalDataSource
import ca.hojat.gamehub.feature_settings.domain.entities.Settings
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject
import javax.inject.Singleton

interface SaveSettingsUseCase : UseCase<Settings, Unit>

@Singleton
@BindType
class SaveSettingsUseCaseImpl @Inject constructor(
    private val localDataStore: SettingsLocalDataSource,
) : SaveSettingsUseCase {

    override suspend fun execute(params: Settings) {
        localDataStore.saveSettings(params)
    }
}
