package dk.thrane.compiler.ast

import dk.thrane.compiler.ast.Tokens.*

class Parser() {
    fun parse() {
        val source = "func                 thisIsAFuncIdentifier (      ) end thisIsAFuncIdentifier"
        var cursor = Cursor(source)

        function(cursor)
    }

    private fun function(cursor: Cursor) {
        functionHead(cursor)
        functionBody(cursor)
        functionTail(cursor)
    }

    private fun functionHead(cursor: Cursor) {
        Tokens.consume(T_FUNC, cursor)
        var id = Tokens.consume(T_ID, cursor)
        Tokens.consume(T_LPAR, cursor)
        Tokens.consume(T_RPAR, cursor)

        println(id.image)
    }

    private fun functionBody(cursor: Cursor) {

    }

    private fun functionTail(cursor: Cursor) {
        Tokens.consume(T_END, cursor)
        var id = Tokens.consume(T_ID, cursor)
        println(id.image)
    }
}

fun main(args: Array<String>) {
    Parser().parse()
}