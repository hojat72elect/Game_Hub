package ca.hojat.gamehub.core.sharers

import android.content.Context
import android.content.Intent
import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.providers.StringProvider
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface TextSharer : ContentSharer<String>

@BindType
class TextSharerImpl @Inject constructor(
    private val stringProvider: StringProvider
) : TextSharer {

    private companion object {
        private const val DATA_TYPE_TEXT = "text/*"
    }

    override fun share(context: Context, content: String) {
        val textSharingIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = DATA_TYPE_TEXT
            putExtra(Intent.EXTRA_TEXT, content)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent.createChooser(
                textSharingIntent,
                stringProvider.getString(R.string.action_share_via)
            )
        )
    }
}
