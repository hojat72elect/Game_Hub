package ca.hojat.gamehub.feature_settings.data.datastores

import androidx.datastore.core.DataStore
import ca.hojat.gamehub.feature_settings.domain.datastores.SettingsLocalDataSource
import ca.hojat.gamehub.feature_settings.domain.entities.Settings
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BindType
class SettingsFileDataSource @Inject constructor(
    private val protoDataSource: DataStore<ProtoSettings>,
    private val protoMapper: ProtoSettingsMapper,
) : SettingsLocalDataSource {

    override suspend fun saveSettings(settings: Settings) {
        protoDataSource.updateData {
            protoMapper.mapToProtoSettings(settings)
        }
    }

    override fun observeSettings(): Flow<Settings> {
        return protoDataSource.data
            .map(protoMapper::mapToDomainSettings)
    }
}
