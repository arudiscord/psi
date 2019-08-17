package pw.aru.utils.extensions.lib

import com.grack.nanojson.JsonArray
import com.grack.nanojson.JsonObject
import pw.aru.utils.Json

fun jsonOf(vararg pairs: Pair<String, *>) = if (pairs.isNotEmpty()) JsonObject(mapOf(*pairs)) else JsonObject()

fun jsonArrayOf(vararg contents: Any?) = JsonArray(contents.toList())

fun jsonStringOf(vararg pairs: Pair<String, *>) = jsonOf(*pairs).toString()

fun jsonArrayStringOf(vararg contents: Any?) = jsonArrayOf(*contents).toString()

fun Map<String, *>.asJsonObject() = JsonObject(this)

fun Iterable<Pair<String, *>>.asJsonObject() = JsonObject(this.toMap())

fun Iterable<*>.asJsonArray() = JsonArray(this.toList())

fun Sequence<*>.asJsonArray() = JsonArray(this.toList())

fun String.toJsonObject() = Json.objectParser().from(this)

fun String.toJsonArray() = Json.arrayParser().from(this)
