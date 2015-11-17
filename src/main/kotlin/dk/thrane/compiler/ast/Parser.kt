package dk.thrane.compiler.ast

import dk.thrane.compiler.ast.Tokens.*
import java.util.*

class Parser() {
    fun parse() {
        val source = """func                 thisIsAFuncIdentifier (  test: int, test2: array of record of {test2: array of int}    )
            var a:int,b:bool,c:char;
            var another:bool, anArray: array of int;
            write anArray[a];
            return false;
        end thisIsAFuncIdentifier"""
        var cursor = Cursor(source)

        println(function(cursor))
    }

    private fun function(cursor: Cursor): FunctionNode {
        val lineNumber = cursor.lineNumber
        val head = functionHead(cursor)
        val body = functionBody(cursor)
        val tail = functionTail(cursor)
        return FunctionNode(lineNumber, head, body, tail)
    }

    private fun functionHead(cursor: Cursor): FunctionHead {
        Tokens.consume(T_FUNC, cursor)
        val lineNumber = cursor.lineNumber
        var id = Tokens.consume(T_ID, cursor)
        var parameters = ArrayList<FieldDeclarationNode>()

        Tokens.consume(T_LPAR, cursor)
        variableDeclarationList(cursor, parameters)
        Tokens.consume(T_RPAR, cursor)

        return FunctionHead(lineNumber, id.image, parameters)
    }

    private fun functionBody(cursor: Cursor): FunctionBody {
        val lineNumber = cursor.lineNumber
        val declarations = ArrayList<DeclarationNode>()

        outer@while (true) {
            val next = Tokens.nextToken(cursor, false)
            when (next.tokenType) {
                T_VAR, T_TYPE, T_FUNC -> declarations.add(declaration(cursor))
                else -> break@outer
            }
        }
        return FunctionBody(lineNumber, declarations)
    }

    private fun variableDeclarationList(cursor: Cursor, list: MutableList<FieldDeclarationNode>) {
        var next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber
        when (next.tokenType) {
            T_ID -> {
                var id = Tokens.consume(T_ID, cursor)
                Tokens.consume(T_COLON, cursor)
                val typeNode = type(cursor)
                list.add(FieldDeclarationNode(lineNumber, id.image, typeNode))
                Tokens.optionallyConsume(T_COMMA, cursor)
                variableDeclarationList(cursor, list)
            }
            else -> {
            }
        }
    }

    private fun type(cursor: Cursor): TypeNode {
        var next = Tokens.nextToken(cursor)
        val lineNumber = cursor.lineNumber
        when (next.tokenType) {
            T_ID, T_INT, T_BOOL, T_CHAR -> return TypeNode(lineNumber, next.tokenType)
            T_ARRAY -> return ArrayTypeNode(lineNumber, type(cursor))
            T_RECORD -> {
                val fields = ArrayList<FieldDeclarationNode>()
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

    private fun declaration(cursor: Cursor): DeclarationNode {
        val next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber

        when (next.tokenType) {
            T_TYPE -> {
                Tokens.consume(T_TYPE, cursor)
                val id = Tokens.consume(T_ID, cursor)
                Tokens.consume(T_EQUAL, cursor)
                val type = type(cursor)
                Tokens.consume(T_SCOLON, cursor)
                return TypeDeclarationNode(lineNumber, id.image, type)
            }
            T_VAR -> {
                val variables = ArrayList<FieldDeclarationNode>()
                Tokens.consume(T_VAR, cursor)
                variableDeclarationList(cursor, variables)
                Tokens.consume(T_SCOLON, cursor)
                return VariableDeclarationNode(lineNumber, variables)
            }
            T_FUNC -> return FunctionDeclarationNode(lineNumber, function(cursor))
            else -> throw IllegalStateException("Unexpected token ${next.tokenType}. Expected one of, $T_TYPE, " +
                    "$T_VAR, $T_FUNC. At line ${cursor.lineNumber}")
        }
    }

    private fun statement(cursor: Cursor): StatementNode {
        val next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber

        when (next.tokenType) {
            T_RETURN -> {
                Tokens.consume(T_RETURN, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_SCOLON, cursor)
                return ReturnNode(lineNumber, expr)
            }
            T_WRITE -> {
                Tokens.consume(T_WRITE, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_SCOLON, cursor)
                return WriteNode(lineNumber, expr)
            }
            T_ALLOC -> {
                Tokens.consume(T_ALLOC, cursor)
                val variable = variable(cursor)
                val peek = Tokens.nextToken(cursor, false)
                var lengthExpression: ExpressionNode? = null
                if (peek.tokenType == T_LENGTH) {
                    Tokens.consume(T_LENGTH, cursor)
                    lengthExpression = expression(cursor)
                }
                Tokens.consume(T_SCOLON, cursor)
                return AllocNode(lineNumber, variable, lengthExpression)
            }
            T_IF -> {
                Tokens.consume(T_IF, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_THEN, cursor)
                val statement = statement(cursor)
                val peek = Tokens.nextToken(cursor, false)
                var elseStatement: StatementNode? = null
                if (peek.tokenType == T_ELSE) {
                    elseStatement = statement(cursor)
                }
                return IfNode(lineNumber, expr, statement, elseStatement)
            }
            T_WHILE -> {
                Tokens.consume(T_WHILE, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_DO, cursor)
                val statement = statement(cursor)
                return WhileNode(lineNumber, expr, statement)
            }
            T_LBRACE -> {
                Tokens.consume(T_LBRACE, cursor)
                val statements = ArrayList<StatementNode>()
                outer@while (true) {
                    if (statementLookahead(cursor)) {
                        statements.add(statement(cursor))
                    } else {
                        break@outer
                    }
                }
                Tokens.consume(T_RBRACE, cursor)
                return BlockNode(lineNumber, statements)
            }
            T_ID -> {
                val variable = variable(cursor)
                Tokens.consume(T_EQUAL, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_SCOLON, cursor)
                return AssignmentNode(lineNumber, variable, expr)
            }
            else -> throw IllegalStateException("Unexpected token while parsing statement, got ${next.tokenType}. " +
                    "At line ${cursor.lineNumber}")
        }
    }

    private fun statementLookahead(cursor: Cursor): Boolean {
        val peek = Tokens.nextToken(cursor, false)
        when (peek.tokenType) {
            T_RETURN, T_WRITE, T_ALLOC, T_IF, T_WHILE, T_ID -> return true
            else -> return false
        }
    }

    private fun expression(cursor: Cursor): ExpressionNode {

        return ExpressionNode(0)
    }

    private fun term(cursor: Cursor): TermNode {
        val peek = Tokens.peek(cursor, 2)
        val lineNumber = cursor.lineNumber
        when (peek[0].tokenType) {
            T_ID -> {
                when (peek[1].tokenType) {
                    T_LPAR -> {
                        val id = Tokens.consume(T_ID, cursor)
                        Tokens.consume(T_LPAR, cursor)
                        // Expression list
                        Tokens.consume(T_RPAR, cursor)
                        return FunctionCallNode(lineNumber, id.image, emptyList())
                    }
                    else -> {
                        return VariableTermNode(lineNumber, variable(cursor))
                    }
                }
            }
            T_LPAR -> {
                Tokens.consume(T_LPAR, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_RPAR, cursor)
                return ParenthesisTermNode(lineNumber, expr)
            }
            T_BANG -> {
                Tokens.consume(T_BANG, cursor)
                val term = term(cursor)
                return NegationNode(lineNumber, term)
            }
            T_VBAR -> {
                Tokens.consume(T_VBAR, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_VBAR, cursor)
                return AbsoluteNode(lineNumber, expr)
            }
            T_NUM -> {
                // The regex should keep us safe here
                val number = Tokens.consume(T_NUM, cursor)
                val value = Integer.parseInt(number.image)
                return NumberLiteralNode(lineNumber, value)
            }
            T_TRUE -> {
                Tokens.consume(T_TRUE, cursor)
                return BooleanLiteralNode(lineNumber, true)
            }
            T_FALSE -> {
                Tokens.consume(T_FALSE, cursor)
                return BooleanLiteralNode(lineNumber, false)
            }
            T_NULL -> {
                Tokens.consume(T_NULL, cursor)
                return NullLiteralNode(lineNumber)
            }
            else -> throw IllegalStateException("Unexpected token encountered (${peek[0].tokenType}) when parsing " +
                    "term. At line $lineNumber")
        }
    }

    private fun variable(cursor: Cursor): VariableNode {
        val id = Tokens.consume(T_ID, cursor)
        return variableRecursion(cursor, id.image)
    }

    private fun variableRecursion(cursor: Cursor, identifier: String): VariableNode {
        val next = Tokens.nextToken(cursor)
        val lineNumber = cursor.lineNumber
        when (next.tokenType) {
            T_LSBRACE -> {
                val expr = expression(cursor)
                Tokens.consume(T_RSBRACE, cursor)
                return ArrayAccessNode(lineNumber, identifier, expr)
            }
            T_DOT -> {
                val id = Tokens.consume(T_ID, cursor)
                return FieldAccessNode(lineNumber, identifier, id.image)
            }
            else -> throw IllegalStateException("Unexpected token while parsing variable, got ${next.tokenType}. " +
                    "At line ${cursor.lineNumber}")
        }
    }
}

fun main(args: Array<String>) {
    Parser().parse()
}