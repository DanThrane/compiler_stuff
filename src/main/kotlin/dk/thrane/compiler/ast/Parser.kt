package dk.thrane.compiler.ast

import dk.thrane.compiler.ast.Tokens.*

class Parser() {
    fun parse() {
        val source = "func                 thisIsAFuncIdentifier (  test: int, test2: array of record of {test2: array of int}    ) end thisIsAFuncIdentifier"
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
        variableDeclarationList(cursor)
        Tokens.consume(T_RPAR, cursor)

        println(id.image)
    }

    private fun functionBody(cursor: Cursor) {
        var next = Tokens.nextToken(cursor, false)
        when (next.tokenType) {
            T_ID -> variableDeclarationList(cursor)
            T_END -> return
            else -> throw IllegalStateException("Unexpected token expected '${next.tokenType}', expected $T_ID or " +
                    "$T_END")
        }
    }

    private fun variableDeclarationList(cursor: Cursor) {
        var next = Tokens.nextToken(cursor, false)
        when (next.tokenType) {
            T_ID -> {
                var id = Tokens.consume(T_ID, cursor)
                Tokens.consume(T_COLON, cursor)
                type(cursor)
                Tokens.optionallyConsume(T_COMMA, cursor)
                variableDeclarationList(cursor)
            }
            else -> {}
        }
    }

    private fun type(cursor: Cursor): Any {
        var next = Tokens.nextToken(cursor)
        when (next.tokenType) {
            T_ID, T_INT, T_BOOL, T_CHAR -> return next.tokenType
            T_ARRAY -> {
                type(cursor)
                return next.tokenType
            }
            T_RECORD -> {
                Tokens.consume(T_LBRACE, cursor)
                variableDeclarationList(cursor)
                Tokens.consume(T_RBRACE, cursor)
                return next.tokenType
            }
            else -> throw IllegalStateException("Unexpected token '${next.tokenType}'. Expected a type here. " +
                    "At ${cursor.remainingString}")
        }
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