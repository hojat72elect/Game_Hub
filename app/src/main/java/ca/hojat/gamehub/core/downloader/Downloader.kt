package ca.hojat.gamehub.core.downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import ca.hojat.gamehub.core.SdkInfo
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * The API part that performs the download (so we don't have to do it in our ViewModels).
 */
interface Downloader {
    fun downloadFile(
        url: String,
        notificationTitle: String,
        fileName: String,
    ): Long
}

@BindType
class DownloaderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : Downloader {


    private val downloadManager = if (SdkInfo.IS_AT_LEAST_MARSHMALLOW) {
        // Build the DownloadManager in the new way.
        context.getSystemService(DownloadManager::class.java)
    } else {
        // Build the DownloadManager in the old way
       context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun downloadFile(
        url: String, notificationTitle: String, fileName: String
    ): Long {

        val request = DownloadManager.Request(url.toUri()).setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(notificationTitle).setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, fileName
            )

        return downloadManager.enqueue(request)
    }

}