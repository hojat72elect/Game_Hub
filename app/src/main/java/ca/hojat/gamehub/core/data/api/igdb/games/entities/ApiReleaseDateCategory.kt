package ca.hojat.gamehub.core.data.api.igdb.games.entities

import ca.hojat.gamehub.core.data.api.igdb.games.entities.ApiReleaseDateCategory.Companion.asReleaseDateCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ReleaseDateCategorySerializer::class)
enum class ApiReleaseDateCategory(val rawValue: Int) {
    UNKNOWN(rawValue = -1),

    YYYY_MMMM_DD(rawValue = 0),
    YYYY_MMMM(rawValue = 1),
    YYYY(rawValue = 2),
    YYYYQ1(rawValue = 3),
    YYYYQ2(rawValue = 4),
    YYYYQ3(rawValue = 5),
    YYYYQ4(rawValue = 6),

    TBD(rawValue = 7);

    companion object {

        fun Int.asReleaseDateCategory(): ApiReleaseDateCategory {
            return values().find { it.rawValue == this } ?: UNKNOWN
        }
    }
}

object ReleaseDateCategorySerializer : KSerializer<ApiReleaseDateCategory> {

    override val descriptor = PrimitiveSerialDescriptor(
        checkNotNull(ReleaseDateCategorySerializer::class.qualifiedName),
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: ApiReleaseDateCategory) {
        encoder.encodeInt(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): ApiReleaseDateCategory {
        return decoder.decodeInt().asReleaseDateCategory()
    }
}
