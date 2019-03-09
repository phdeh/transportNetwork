fun<T> List<T>.pairs(): List<Pair<T, T>> {
    synchronized(this) {
        if (this.size <= 1)
            return listOf()
        val ml = mutableListOf<Pair<T, T>>()
        val it = this.iterator()
        var prev = it.next()
        while (it.hasNext()) {
            val curr = it.next()
            ml += Pair(prev, curr)
            prev = curr
        }
        return ml
    }
}