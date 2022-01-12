package dev.fredag.cheerwithme.happening

/**
 * Transforms a List to a Map given a function to extract a key from each element
 *
 * If byKey derives the same key from multiple entries in the list only the last entry will remain.
 *
 * @param byKey: Function that extracts the key to use from an entry in the list.
 */
fun <K, T> List<T>.toMapByKey(byKey: (T) -> K): Map<K,T> {
    return this.map { byKey(it) to it }.toMap()
}