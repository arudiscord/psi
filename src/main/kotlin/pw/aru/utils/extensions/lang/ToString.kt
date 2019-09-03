@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

fun <E> Iterable<E>.toSmartString(transform: ((E) -> CharSequence)? = null): String {
    val list = toMutableList()

    if (list.isEmpty()) return "nothing"
    if (list.size == 1) return first().transformElement(transform).toString()
    if (list.size == 2) {
        val (e1, e2) = list
        return "${e1.transformElement(transform)} and ${e2.transformElement(transform)}"
    }
    val last = list.removeAt(list.size - 1)
    return list.joinToString(", ", transform = transform, postfix = " and ${last.transformElement(transform)}")
}

internal fun <T> T.transformElement(transform: ((T) -> CharSequence)?): CharSequence {
    return when {
        transform != null -> transform(this)
        this is CharSequence -> this
        else -> this.toString()
    }
}

fun List<String>.limitedToString(limit: Int): String {
    if (isEmpty()) return "None"
    else {
        val builder = StringBuilder()
        val iterator = listIterator()

        while (iterator.hasNext()) {
            val next = iterator.next()

            if ((builder.length + next.length + 2) < limit) {
                builder.append(next)
                if (iterator.hasNext()) builder.append(", ")
            } else {
                builder.append("more ").append(size - iterator.nextIndex()).append("...")
                break
            }
        }

        return builder.toString()
    }
}
