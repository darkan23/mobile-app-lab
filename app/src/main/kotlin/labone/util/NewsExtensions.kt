package labone.util

fun <T> List<T>.copy(i: Int, value: T): List<T> = toMutableList().apply { set(i, value) }
