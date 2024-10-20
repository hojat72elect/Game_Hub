package ca.hojat.gamehub.core.data.api.igdb.games.entities

import ca.hojat.gamehub.core.data.api.igdb.games.entities.ApiAgeRatingType.Companion.asAgeRatingType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AgeRatingTypeSerializer::class)
enum class ApiAgeRatingType(val rawValue: Int) {
    UNKNOWN(rawValue = -1),
    THREE(rawValue = 1),
    SEVEN(rawValue = 2),
    TWELVE(rawValue = 3),
    SIXTEEN(rawValue = 4),
    EIGHTEEN(rawValue = 5),
    RP(rawValue = 6),
    EC(rawValue = 7),
    E(rawValue = 8),
    E10(rawValue = 9),
    T(rawValue = 10),
    M(rawValue = 11),
    AO(rawValue = 12);

    companion object {

        fun Int.asAgeRatingType(): ApiAgeRatingType {
            return values().find { it.rawValue == this } ?: UNKNOWN
        }
    }
}

object AgeRatingTypeSerializer : KSerializer<ApiAgeRatingType> {

    override val descriptor = PrimitiveSerialDescriptor(
        checkNotNull(AgeRatingTypeSerializer::class.qualifiedName),
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: ApiAgeRatingType) {
        encoder.encodeInt(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): ApiAgeRatingType {
        return decoder.decodeInt().asAgeRatingType()
    }
}
