@file:JvmName("ScrollViewUtils")

package ca.on.hojat.gamenews.shared.commons.ktx.views

import android.view.View
import android.widget.ScrollView

fun ScrollView.scrollToTop() {
    post { fullScroll(View.FOCUS_UP) }
}

fun ScrollView.scrollToBottom() {
    post { fullScroll(View.FOCUS_DOWN) }
}
