@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

/**
 * Splits a [List] as equally as possible within a range.
 *
 * @param minSize the minimum size of any list.
 * @param maxSize the maximum size of any list.
 */
fun <E> List<E>.bestSplit(minSize: Int, maxSize: Int): List<List<E>> {
    require(minSize > 0) { "minSize must be greater than zero." }
    require(maxSize > 0) { "maxSize must be greater than zero." }
    require(minSize < maxSize) { "maxSize must be greater than minSize." }
    return when {
        size < maxSize -> listOf(this)
        else -> chunked((minSize..maxSize).minBy { it - size % it } ?: (minSize + maxSize) / 2)
    }
}

/**
 * Flattens a collection of collections in a [round-robin](https://en.wikipedia.org/wiki/Round-robin_scheduling)
 * fashion.
 */
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

/**
 * Returns a random element from this collection. or null if the collection is empty.
 */
inline fun <E> Collection<E>.randomOrNull(): E? = if (isEmpty()) null else random()

/**
 * Returns a random element from this array, or null if the array is empty.
 */
inline fun <E> Array<E>.randomOrNull(): E? = if (isEmpty()) null else random()

/**
 * Returns a random element of the parameters.
 *
 * @throws NoSuchElementException if no parameters are specified.
 */
inline fun <E> randomOf(vararg objects: E): E = objects.random()
