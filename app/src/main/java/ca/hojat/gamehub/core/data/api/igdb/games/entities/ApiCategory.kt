package ca.hojat.gamehub.core.data.api.igdb.games.entities

import ca.hojat.gamehub.core.data.api.igdb.games.entities.ApiCategory.Companion.asCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CategorySerializer::class)
enum class ApiCategory(val rawValue: Int) {
    UNKNOWN(rawValue = -1),
    MAIN_GAME(rawValue = 0),
    DLC(rawValue = 1),
    EXPANSION(rawValue = 2),
    BUNDLE(rawValue = 3),
    STANDALONE_EXPANSION(rawValue = 4),
    MOD(rawValue = 5),
    EPISODE(rawValue = 6),
    SEASON(rawValue = 7);

    companion object {

        fun Int.asCategory(): ApiCategory {
            return values().find { it.rawValue == this } ?: UNKNOWN
        }
    }
}

object CategorySerializer : KSerializer<ApiCategory> {

    override val descriptor = PrimitiveSerialDescriptor(
        checkNotNull(CategorySerializer::class.qualifiedName),
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: ApiCategory) {
        encoder.encodeInt(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): ApiCategory {
        return decoder.decodeInt().asCategory()
    }
}
