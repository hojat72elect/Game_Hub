@file:JvmName("MapUtils")

package ca.on.hojat.gamenews.shared.commons.ktx

fun <K, V> Map<K, V>.get(key: K, default: V): V {
    return if (containsKey(key)) {
        (get(key) ?: default)
    } else {
        default
    }
}
