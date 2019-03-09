fun<T> List<T>.combinations(): List<List<T>> {
    val combinations = mutableListOf<List<T>>(listOf())
    fun handle(list: List<T>, it: T) {
        val cList = list + it
        combinations += cList
        val max = indexOf(it)
        forEach {
            if (indexOf(it) > max)
                handle(cList, it)
        }
    }
    forEach {
        handle(listOf(), it)
    }
    return combinations.toList()
}

fun main(args: Array<String>) {
    println(listOf(1, 2, 3).combinations())
}