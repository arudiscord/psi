@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

inline fun <T, U> T.applyOn(thisObj: U, block: U.() -> Unit): T {
    thisObj.block()
    return this
}

