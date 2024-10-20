package ca.hojat.gamehub.core.data.api.igdb.games.entities

import ca.hojat.gamehub.core.data.api.igdb.games.entities.ApiAgeRatingCategory.Companion.asAgeRatingCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AgeRatingCategorySerializer::class)
enum class ApiAgeRatingCategory(val rawValue: Int) {
    UNKNOWN(rawValue = -1),
    ESRB(rawValue = 1),
    PEGI(rawValue = 2);

    companion object {

        fun Int.asAgeRatingCategory(): ApiAgeRatingCategory {
            return values().find { it.rawValue == this } ?: UNKNOWN
        }
    }
}

object AgeRatingCategorySerializer : KSerializer<ApiAgeRatingCategory> {

    override val descriptor = PrimitiveSerialDescriptor(
        checkNotNull(AgeRatingCategorySerializer::class.qualifiedName),
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: ApiAgeRatingCategory) {
        encoder.encodeInt(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): ApiAgeRatingCategory {
        return decoder.decodeInt().asAgeRatingCategory()
    }
}
