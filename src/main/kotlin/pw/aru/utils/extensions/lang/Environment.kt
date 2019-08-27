package pw.aru.utils.extensions.lang

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object Environment : ReadOnlyProperty<Any?, String> {
    private val env by lazy { System.getenv() }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return env.get(property.name) ?: throw IllegalStateException("No environment property ${property.name}")
    }
}
