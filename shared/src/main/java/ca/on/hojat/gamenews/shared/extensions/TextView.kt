@file:JvmName("TextViewUtils")

package ca.on.hojat.gamenews.shared.extensions

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.TextView

private const val COMPOUND_DRAWABLE_INDEX_LEFT = 0
private const val COMPOUND_DRAWABLE_INDEX_TOP = 1
private const val COMPOUND_DRAWABLE_INDEX_RIGHT = 2
private const val COMPOUND_DRAWABLE_INDEX_BOTTOM = 3

var TextView.startDrawable: Drawable?
    set(value) = updateCompoundDrawable(start = value)
    get() = compoundDrawablesRelative[COMPOUND_DRAWABLE_INDEX_LEFT]

var TextView.topDrawable: Drawable?
    set(value) = updateCompoundDrawable(top = value)
    get() = compoundDrawablesRelative[COMPOUND_DRAWABLE_INDEX_TOP]

var TextView.endDrawable: Drawable?
    set(value) = updateCompoundDrawable(end = value)
    get() = compoundDrawablesRelative[COMPOUND_DRAWABLE_INDEX_RIGHT]

var TextView.bottomDrawable: Drawable?
    set(value) = updateCompoundDrawable(bottom = value)
    get() = compoundDrawablesRelative[COMPOUND_DRAWABLE_INDEX_BOTTOM]

fun TextView.updateCompoundDrawable(
    start: Drawable? = startDrawable,
    top: Drawable? = topDrawable,
    end: Drawable? = endDrawable,
    bottom: Drawable? = bottomDrawable
) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
}

fun TextView.enableMultilineText() {
    minLines = 0
    maxLines = Integer.MAX_VALUE
    isSingleLine = false
}

fun TextView.disableMultilineText() {
    minLines = 1
    maxLines = 1
    isSingleLine = true
}

fun TextView.setSingleLineTextEnabled(isSingleLineTextEnabled: Boolean) {
    if (isSingleLineTextEnabled) {
        disableMultilineText()
    } else {
        enableMultilineText()
    }
}

fun TextView.setTextSizeInPx(size: Float) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
}
