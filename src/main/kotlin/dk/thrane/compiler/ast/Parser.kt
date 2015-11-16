package dk.thrane.compiler.ast

import dk.thrane.compiler.ast.Tokens.*
import java.util.*

class Parser() {
    fun parse() {
        val source = "func                 thisIsAFuncIdentifier (  test: int, test2: array of record of {test2: array of int}    ) end thisIsAFuncIdentifier"
        var cursor = Cursor(source)

        println(function(cursor))
    }

    private fun function(cursor: Cursor): Function {
        val lineNumber = cursor.lineNumber
        val head = functionHead(cursor)
        val body = functionBody(cursor)
        val tail = functionTail(cursor)
        return Function(lineNumber, head, body, tail)
    }

    private fun functionHead(cursor: Cursor): FunctionHead {
        Tokens.consume(T_FUNC, cursor)
        val lineNumber = cursor.lineNumber
        var id = Tokens.consume(T_ID, cursor)
        var parameters = ArrayList<VariableDeclarationNode>()

        Tokens.consume(T_LPAR, cursor)
        variableDeclarationList(cursor, parameters)
        Tokens.consume(T_RPAR, cursor)

        return FunctionHead(lineNumber, id.image, parameters)
    }

    private fun functionBody(cursor: Cursor): FunctionBody {
        val lineNumber = cursor.lineNumber
        return FunctionBody(lineNumber)
    }

    private fun variableDeclarationList(cursor: Cursor, list: MutableList<VariableDeclarationNode>) {
        var next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber
        when (next.tokenType) {
            T_ID -> {
                var id = Tokens.consume(T_ID, cursor)
                Tokens.consume(T_COLON, cursor)
                val typeNode = type(cursor)
                list.add(VariableDeclarationNode(lineNumber, id.image, typeNode))
                Tokens.optionallyConsume(T_COMMA, cursor)
                variableDeclarationList(cursor, list)
            }
            else -> {}
        }
    }

    private fun type(cursor: Cursor): TypeNode {
        var next = Tokens.nextToken(cursor)
        val lineNumber = cursor.lineNumber
        when (next.tokenType) {
            T_ID, T_INT, T_BOOL, T_CHAR -> return TypeNode(lineNumber, next.tokenType)
            T_ARRAY -> return ArrayTypeNode(lineNumber, type(cursor))
            T_RECORD -> {
                val fields = ArrayList<VariableDeclarationNode>()
                Tokens.consume(T_LBRACE, cursor)
                variableDeclarationList(cursor, fields)
                Tokens.consume(T_RBRACE, cursor)
                return RecordTypeNode(lineNumber, fields)
            }
            else -> throw IllegalStateException("Unexpected token '${next.tokenType}'. Expected a type here. " +
                    "At ${cursor.remainingString}")
        }
    }

    private fun functionTail(cursor: Cursor): FunctionTail {
        val lineNumber = cursor.lineNumber
        Tokens.consume(T_END, cursor)
        var id = Tokens.consume(T_ID, cursor)
        return FunctionTail(lineNumber, id.image)
    }
}

fun main(args: Array<String>) {
    Parser().parse()
}