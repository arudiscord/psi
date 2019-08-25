@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

fun <E> List<E>.bestSplit(minSize: Int, maxSize: Int): List<List<E>> {
    return when {
        size < maxSize -> listOf(this)
        else -> chunked((minSize..maxSize).minBy { it - size % it } ?: (minSize + maxSize) / 2)
    }
}

fun <T> Iterable<Iterable<T>>.roundRobinFlatten(): List<T> {
    val result = ArrayList<T>()
    val iterators = mapTo(ArrayList(), Iterable<T>::iterator)

    while (iterators.isNotEmpty()) {
        val i = iterators.iterator()
        while (i.hasNext()) {
            val ii = i.next()
            if (ii.hasNext()) {
                result.add(ii.next())
            } else {
                i.remove()
            }
        }
    }

    return result
}

inline fun <K, V> Map<K, V>.ifContains(k: K, function: (V) -> Unit) {
    if (containsKey(k)) function(get(k)!!)
}

inline fun <E> List<E>.randomOrNull(): E? = if (isEmpty()) null else random()

inline fun <E> Array<E>.randomOrNull(): E? = if (isEmpty()) null else random()

inline fun <E> randomOf(vararg objects: E): E = objects.random()
