package ca.hojat.gamehub.feature_settings.data

import androidx.datastore.core.DataStore
import ca.hojat.gamehub.feature_settings.DOMAIN_SETTINGS
import ca.hojat.gamehub.feature_settings.data.datastores.ProtoSettings
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_settings.data.datastores.ProtoSettingsMapper
import ca.hojat.gamehub.feature_settings.data.datastores.SettingsFileDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

private val PROTO_SETTINGS = ProtoSettings.newBuilder()
    .setThemeName(DOMAIN_SETTINGS.theme.name)
    .build()

class SettingsFileDataSourceTest {

    @MockK
    private lateinit var protoDataStore: DataStore<ProtoSettings>

    private lateinit var sut: SettingsFileDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = SettingsFileDataSource(
            protoDataSource = protoDataStore,
            protoMapper = ProtoSettingsMapper(),
        )
    }

    @Test
    fun `Saves settings successfully`() {
        runTest {
            coEvery { protoDataStore.updateData(any()) } returns PROTO_SETTINGS

            sut.saveSettings(DOMAIN_SETTINGS)

            coVerify { protoDataStore.updateData(any()) }
        }
    }

}
