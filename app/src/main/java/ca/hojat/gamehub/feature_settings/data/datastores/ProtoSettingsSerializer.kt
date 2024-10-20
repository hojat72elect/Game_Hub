package ca.hojat.gamehub.feature_settings.data.datastores

import androidx.datastore.core.Serializer
import ca.hojat.gamehub.feature_settings.domain.DomainSettings
import java.io.InputStream
import java.io.OutputStream

object ProtoSettingsSerializer : Serializer<ProtoSettings> {

    override val defaultValue: ProtoSettings
        get() = ProtoSettings.newBuilder()
            .setThemeName(DomainSettings.DEFAULT.theme.name)
            .setLanguageName(DomainSettings.DEFAULT.language.name)
            .build()

    override suspend fun writeTo(t: ProtoSettings, output: OutputStream) = t.writeTo(output)
    override suspend fun readFrom(input: InputStream): ProtoSettings =
        ProtoSettings.parseFrom(input)
}
