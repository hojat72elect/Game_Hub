package ca.hojat.gamehub.core.data.api.igdb.games.entities

import ca.hojat.gamehub.core.data.api.igdb.games.entities.ApiWebsiteCategory.Companion.asWebsiteCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = WebsiteCategorySerializer::class)
enum class ApiWebsiteCategory(val rawValue: Int) {
    UNKNOWN(rawValue = -1),
    OFFICIAL(rawValue = 1),
    FANDOM(rawValue = 2),
    WIKIPEDIA(rawValue = 3),
    FACEBOOK(rawValue = 4),
    TWITTER(rawValue = 5),
    TWITCH(rawValue = 6),
    INSTAGRAM(rawValue = 8),
    YOUTUBE(rawValue = 9),
    APP_STORE(rawValue = 10),
    GOOGLE_PLAY(rawValue = 12),
    STEAM(rawValue = 13),
    SUBREDDIT(rawValue = 14),
    EPIC_GAMES(rawValue = 16),
    GOG(rawValue = 17),
    DISCORD(rawValue = 18);

    companion object {

        fun Int.asWebsiteCategory(): ApiWebsiteCategory {
            return values().find { it.rawValue == this } ?: UNKNOWN
        }
    }
}

object WebsiteCategorySerializer : KSerializer<ApiWebsiteCategory> {

    override val descriptor = PrimitiveSerialDescriptor(
        checkNotNull(WebsiteCategorySerializer::class.qualifiedName),
        PrimitiveKind.INT
    )

    override fun serialize(encoder: Encoder, value: ApiWebsiteCategory) {
        encoder.encodeInt(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): ApiWebsiteCategory {
        return decoder.decodeInt().asWebsiteCategory()
    }
}
