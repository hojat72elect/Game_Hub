package ca.hojat.gamehub.feature_news.data.datastores.gamespot

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class ArticlePublicationDateMapper @Inject constructor() {

    companion object {
        private const val PUBLICATION_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

        // The best we can do here, since the API does not return
        // the time zone for some reason.
        private const val PUBLICATION_DATE_TIME_ZONE = "America/Los_Angeles"
    }

    fun mapToTimestamp(publicationDate: String): Long {
        return LocalDateTime.parse(
            publicationDate,
            DateTimeFormatter.ofPattern(PUBLICATION_DATE_FORMAT)
        )
            .atZone(ZoneId.of(PUBLICATION_DATE_TIME_ZONE))
            .toInstant()
            .toEpochMilli()
    }
}
