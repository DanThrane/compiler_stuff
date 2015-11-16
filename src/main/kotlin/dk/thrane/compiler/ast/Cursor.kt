package dk.thrane.compiler.ast

class Cursor(val source: String) {
    private var idx: Int = 0

    val remainingString: String
        get() = source.substring(idx)

    fun advance(count: Int) {
        idx += count
    }
}