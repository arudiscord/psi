package pw.aru.utils

import com.grack.nanojson.JsonArray
import com.grack.nanojson.JsonObject
import com.grack.nanojson.JsonParser

object Json {
    fun parser() = JsonParser.any()

    fun arrayParser() = JsonParser.array()

    fun objectParser() = JsonParser.`object`()

    fun arrayBuilder() = JsonArray.builder()

    fun objectBuilder() = JsonObject.builder()
}