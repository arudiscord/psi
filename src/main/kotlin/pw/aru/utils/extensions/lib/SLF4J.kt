package pw.aru.utils.extensions.lib

import org.slf4j.MDC

fun <T, R> T.withMDC(vararg pairs: Pair<String, String>, block: T.() -> R) {
    for ((k, v) in pairs) MDC.put(k, v)
    try {
        block()
    } finally {
        for ((k) in pairs) MDC.remove(k)
    }
}
