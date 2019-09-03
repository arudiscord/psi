package pw.aru.utils.extensions.lang

import pw.aru.utils.extensions.lang.Environment.orDefault
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Utility class that wraps [System.getenv] to provide [read-only properties][ReadOnlyProperty],
 * with [optional default values][orDefault] and lazy-loading of the environment' map.
 */
object Environment : ReadOnlyProperty<Any?, String> {
    private val env by lazy { System.getenv() }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return property.run { env[name] ?: throw IllegalStateException("No environment property $name") }
    }

    /**
     * Returns a [ReadOnlyProperty] which defaults to a string if the
     * environment property is not found.
     */
    fun orDefault(default: String): ReadOnlyProperty<Any?, String> {
        return object : ReadOnlyProperty<Any?, String> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): String {
                return property.run { getOrDefault(name, default) }
            }
        }
    }

    /**
     * Gets the value of the specified environment variable, or
     * **null** if the environment property is not found.
     *
     * @param name the name of the environment variable.
     */
    operator fun get(name: String): String? {
        return env[name]
    }

    /**
     * Gets the value of the specified environment variable, or
     * a default value if the environment property is not found.
     *
     * @param name the name of the environment variable.
     * @param default the default value.
     */
    fun getOrDefault(name: String, default: String): String {
        return env.getOrDefault(name, default)
    }
}
