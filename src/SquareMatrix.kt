import java.lang.StringBuilder
import java.lang.UnsupportedOperationException

interface Matrix<Key, Value> {
    val keys: List<Key>
    val size: Int
    operator fun get(x: Int, y: Int): Value
    operator fun get(x: Key, y: Key): Value
    fun toMatrix(): Matrix<Key, Value>
    fun toMutableMatrix(): MutableMatrix<Key, Value>
    fun transpose(): Matrix<Key, Value>
}

interface MutableMatrix<Key, Value> : Matrix<Key, Value> {
    operator fun set(x: Int, y: Int, value: Value)
    operator fun set(x: Key, y: Key, value: Value)
}

class SquareMatrix<Key, Value>: MutableMatrix<Key, Value> {
    override val keys: List<Key>
        get() = privateKeys
    private val privateKeys: List<Key>
    override val size: Int
        get() = privateSize
    private val privateSize: Int
    private val matrix: List<MutableList<Value>>

    constructor(keys: List<Key>, func: (Key, Key) -> Value) {
        this.privateKeys = keys
        this.matrix = List(keys.size) { x ->
            MutableList<Value>(keys.size) { y ->
                func(keys[x], keys[y])
            }
        }
        privateSize = keys.size
    }

    constructor(keys: List<Key>, func: () -> Value) {
        this.privateKeys = keys
        this.matrix = List(keys.size) { x ->
            MutableList<Value>(keys.size) { y ->
                func()
            }
        }
        privateSize = keys.size
    }

    override operator fun get(x: Int, y: Int): Value {
        if (x >= 0 && y >= 0 && x < size && y < size)
            return matrix[x][y]
        else
            throw ArrayIndexOutOfBoundsException("$x, $y")
    }

    override operator fun set(x: Int, y: Int, value: Value) {
        if (x >= 0 && y >= 0 && x < size && y < size)
            matrix[x][y] = value
        else
            throw ArrayIndexOutOfBoundsException("$x, $y")
    }

    override operator fun get(x: Key, y: Key): Value {
        val x1 = keys.indexOf(x)
        val y1 = keys.indexOf(y)
        return get(x1, y1)
    }

    override operator fun set(x: Key, y: Key, value: Value) {
        val x1 = keys.indexOf(x)
        val y1 = keys.indexOf(y)
        set(x1, y1, value)
    }

    override fun toString(): String {
        var maxLength = 0
        val matrix = List(size + 1) { x ->
            List(size + 1) { y ->
                val line = if (x == 0 && y == 0)
                    ""
                else if (x == 0 && y > 0 || y == 0 && x > 0)
                    keys[x + y - 1].toString()
                else
                    get(x - 1, y - 1).toString()
                val length = line.length
                if (maxLength < length)
                    maxLength = length
                line
            }
        }
        maxLength += 1 + maxLength % 2
        val sb = StringBuilder()
        for (y in 0..size) {
            for (x in (0..size)) {
                val prefix =
                    if (x == 1) when (y) {
                        0 -> " "
                        1 -> "⎛"
                        size -> "⎝"
                        else -> "⎜"
                    } else ""
                val suffix =
                    if (x == size) when (y) {
                        0 -> " "
                        1 -> "⎞"
                        size -> "⎠"
                        else -> "⎟"
                    } else ""
                val line = matrix[x][y]
                val lineLength = maxLength - line.length
                sb.append(prefix)
                for (i in 1..(lineLength / 2))
                    sb.append(' ')
                sb.append(line)
                for (i in 1..(lineLength / 2 + lineLength % 2))
                    sb.append(' ')
                sb.append(suffix)
            }
            if (y != size)
            sb.appendln()
        }
        return sb.toString()
    }

    override fun toMatrix(): Matrix<Key, Value> {
        return SquareMatrix<Key, Value>(keys) { x, y ->
            this[x, y]
        }
    }

    override fun toMutableMatrix(): MutableMatrix<Key, Value> {
        return SquareMatrix<Key, Value>(keys) { x, y ->
            this[x, y]
        }
    }

    override fun transpose(): Matrix<Key, Value> {
        return SquareMatrix<Key, Value>(keys) { x, y ->
            this[y, x]
        }
    }
}