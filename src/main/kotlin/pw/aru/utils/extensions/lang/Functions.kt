@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

/**
 * Calls the specified function [block] with [thisObj] value as its receiver and returns `this` value.
 */
inline fun <T, U> T.applyOn(thisObj: U, block: U.() -> Unit): T {
    thisObj.block()
    return this
}

