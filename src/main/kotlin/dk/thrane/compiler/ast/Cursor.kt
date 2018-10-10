package dk.thrane.compiler.ast

class Cursor(val source: String) {
    var idx: Int = 0
    var lineNumber: Int = 1
        private set
    val empty: Boolean
        get() = remainingString.replace(Regex("[ \\n\\t]+"), "").isEmpty()

    val remainingString: String
        get() = source.substring(idx)

    fun advance(count: Int) {
        lineNumber += source.substring(idx, idx + count).map { if (it == '\n') 1 else 0 }.sum()
        idx += count
    }

    fun withdraw(count: Int) {
        lineNumber -= source.substring(idx - count, idx).map { if (it == '\n') 1 else 0 }.sum()
        idx -= count
    }
}
