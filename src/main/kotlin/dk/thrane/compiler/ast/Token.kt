package dk.thrane.compiler.ast

data class Token(val tokenType: Tokens, val image: String, val value: Any? = null) {
    val size: Int
        get() = image.length
}
