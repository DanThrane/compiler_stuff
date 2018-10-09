package dk.thrane.compiler.ast

import dk.thrane.compiler.ast.Tokens.*
import java.util.*

class Parser {
    fun parse(source: String): SourceFileNode {
        val cursor = Cursor(source)
        return SourceFileNode(0, program(cursor))
    }

    private fun program(cursor: Cursor): List<FunctionNode> {
        val result = ArrayList<FunctionNode>()
        while (!cursor.empty) {
            result.add(function(cursor))
        }
        return result
    }

    private fun function(cursor: Cursor): FunctionNode {
        val lineNumber = cursor.lineNumber
        val head = functionHead(cursor)
        val body = functionBody(cursor)
        val tail = functionTail(cursor)
        return FunctionNode(lineNumber, head, body, tail)
    }

    private fun functionHead(cursor: Cursor): FunctionHead {
        Tokens.consume(T_FUNCTION, cursor)
        val lineNumber = cursor.lineNumber
        val id = Tokens.consume(T_ID, cursor)
        val parameters = ArrayList<FieldDeclarationNode>()

        Tokens.consume(T_LEFT_PARENTHESIS, cursor)
        variableDeclarationList(cursor, parameters)
        Tokens.consume(T_RIGHT_PARENTHESIS, cursor)

        val hasType = Tokens.optionallyConsume(Tokens.T_COLON, cursor) != null
        val type = if (hasType) type(cursor) else null
        return FunctionHead(lineNumber, id.image, parameters, type)
    }

    private fun functionBody(cursor: Cursor): FunctionBody {
        val lineNumber = cursor.lineNumber
        val declarations = ArrayList<DeclarationNode>()
        val statements = ArrayList<StatementNode>()

        outer@while (true) {
            val next = Tokens.nextToken(cursor, false)
            when (next.tokenType) {
                T_VAR, T_TYPE, T_FUNCTION -> declarations.add(declaration(cursor))
                else -> break@outer
            }
        }
        while (Tokens.nextToken(cursor, false).tokenType != T_END) {
            statements.add(statement(cursor))
        }
        return FunctionBody(lineNumber, declarations, statements)
    }

    private fun variableDeclarationList(cursor: Cursor, list: MutableList<FieldDeclarationNode>) {
        val next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber
        when (next.tokenType) {
            T_ID -> {
                val id = Tokens.consume(T_ID, cursor)
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
        val next = Tokens.nextToken(cursor)
        val lineNumber = cursor.lineNumber
        return when (next.tokenType) {
            T_INT, T_BOOL, T_CHAR -> TypeNode(lineNumber, next.tokenType)
            T_ID -> IdentifierType(lineNumber, ((next.value as String?)!!))
            T_ARRAY -> ArrayTypeNode(lineNumber, type(cursor))
            T_RECORD -> {
                val fields = ArrayList<FieldDeclarationNode>()
                Tokens.consume(T_LEFT_CURLY_BRACE, cursor)
                variableDeclarationList(cursor, fields)
                Tokens.consume(T_RIGHT_CURLY_BRACE, cursor)
                RecordTypeNode(lineNumber, fields)
            }
            else -> throw IllegalStateException("Unexpected token '${next.tokenType}'. Expected a type here. " +
                    "At ${cursor.remainingString}")
        }
    }

    private fun functionTail(cursor: Cursor): FunctionTail {
        val lineNumber = cursor.lineNumber
        Tokens.consume(T_END, cursor)
        val id = Tokens.consume(T_ID, cursor)
        return FunctionTail(lineNumber, id.image)
    }

    private fun declaration(cursor: Cursor): DeclarationNode {
        val next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber

        when (next.tokenType) {
            T_TYPE -> {
                Tokens.consume(T_TYPE, cursor)
                val id = Tokens.consume(T_ID, cursor)
                Tokens.consume(T_ASSIGNMENT, cursor)
                val type = type(cursor)
                Tokens.consume(T_SEMI_COLON, cursor)
                return TypeDeclarationNode(lineNumber, id.image, type)
            }
            T_VAR -> {
                val variables = ArrayList<FieldDeclarationNode>()
                Tokens.consume(T_VAR, cursor)
                variableDeclarationList(cursor, variables)
                Tokens.consume(T_SEMI_COLON, cursor)
                return VariableDeclarationNode(lineNumber, variables)
            }
            else -> throw IllegalStateException("Unexpected token ${next.tokenType}. Expected one of, $T_TYPE, " +
                    "$T_VAR, $T_FUNCTION. At line ${cursor.lineNumber}")
        }
    }

    private fun statement(cursor: Cursor): StatementNode {
        val next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber

        when (next.tokenType) {
            T_RETURN -> {
                Tokens.consume(T_RETURN, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_SEMI_COLON, cursor)
                return ReturnNode(lineNumber, expr)
            }
            T_WRITE -> {
                Tokens.consume(T_WRITE, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_SEMI_COLON, cursor)
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
                Tokens.consume(T_SEMI_COLON, cursor)
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
                    Tokens.consume(T_ELSE, cursor)
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
            T_LEFT_CURLY_BRACE -> {
                Tokens.consume(T_LEFT_CURLY_BRACE, cursor)
                val statements = ArrayList<StatementNode>()
                outer@while (true) {
                    if (statementLookahead(cursor)) {
                        statements.add(statement(cursor))
                    } else {
                        break@outer
                    }
                }
                Tokens.consume(T_RIGHT_CURLY_BRACE, cursor)
                return BlockNode(lineNumber, statements)
            }
            T_ID -> {
                val variable = variable(cursor)
                Tokens.consume(T_ASSIGNMENT, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_SEMI_COLON, cursor)
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

    private fun findOperator(token: Tokens): Operator? {
        return Operator.values().find { it.token == token }
    }

    private fun expression(cursor: Cursor): ExpressionNode {
        val lineNumber = cursor.lineNumber
        val left = TermExpressionNode(lineNumber, term(cursor))
        val combined = expressionRecursion(cursor, left, 0)
        return combined
    }


    private fun expressionRecursion(cursor: Cursor, left: ExpressionNode, minPrecedence: Int): ExpressionNode {
        val lineNumber = cursor.lineNumber
        var lookahead = Tokens.nextToken(cursor, false)
        var op = findOperator(lookahead.tokenType)
        var result = left

        while (op != null && op.precedense >= minPrecedence) {
            val oldOp = op
            Tokens.nextToken(cursor, true)
            var rhs: ExpressionNode = TermExpressionNode(lineNumber, term(cursor))
            lookahead = Tokens.nextToken(cursor, false)
            op = findOperator(lookahead.tokenType)
            while (op != null && op.precedense > oldOp.precedense) {
                rhs = expressionRecursion(cursor, rhs, op.precedense)
                lookahead = Tokens.nextToken(cursor, false)
                op = findOperator(lookahead.tokenType)
            }

            result = when (oldOp) {
                Operator.MULTIPLICATION -> MultiplicationNode(lineNumber, left, rhs)
                Operator.DIVISION -> DivisionNode(lineNumber, left, rhs)
                Operator.MODULO -> ModuloNode(lineNumber, left, rhs)
                Operator.ADD -> AddNode(lineNumber, left, rhs)
                Operator.MINUS -> MinusNode(lineNumber, left, rhs)
                Operator.COMPARE_LESS_THAN -> CompareLessThanNode(lineNumber, left, rhs)
                Operator.COMPARE_GREATER_THAN -> CompareGreaterThanNode(lineNumber, left, rhs)
                Operator.COMPARE_LESS_THAN_OR_EQUALS -> CompareLessThanOrEqualsNode(lineNumber, left, rhs)
                Operator.COMPARE_GREATER_THAN_OR_EQUALS -> CompareGreaterThanOrEqualsNode(lineNumber, left, rhs)
                Operator.COMPARE_EQUAL -> CompareEqualNode(lineNumber, left, rhs)
                Operator.COMPARE_NOT_EQUAL -> CompareNotEqualNode(lineNumber, left, rhs)
                Operator.AND -> AndNode(lineNumber, left, rhs)
                Operator.OR -> OrNode(lineNumber, left, rhs)
            }
        }
        return result
    }

    private fun expressionList(cursor: Cursor): MutableList<ExpressionNode> {
        val list = ArrayList<ExpressionNode>()
        if (!termLookahead(cursor)) return ArrayList() // All expressions must start with a term
        while (true) {
            list.add(expression(cursor))
            if (Tokens.optionallyConsume(T_COMMA, cursor) == null) {
                break
            }
        }
        return list
    }

    private fun term(cursor: Cursor): TermNode {
        val peek = Tokens.peek(cursor, 2)
        val lineNumber = cursor.lineNumber
        when (peek[0].tokenType) {
            T_ID -> {
                when (peek[1].tokenType) {
                    T_LEFT_PARENTHESIS -> {
                        val id = Tokens.consume(T_ID, cursor)
                        Tokens.consume(T_LEFT_PARENTHESIS, cursor)
                        val arguments = expressionList(cursor)
                        Tokens.consume(T_RIGHT_PARENTHESIS, cursor)
                        return FunctionCallNode(lineNumber, id.image, arguments)
                    }
                    else -> {
                        return VariableTermNode(lineNumber, variable(cursor))
                    }
                }
            }
            T_LEFT_PARENTHESIS -> {
                Tokens.consume(T_LEFT_PARENTHESIS, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_RIGHT_PARENTHESIS, cursor)
                return ParenthesisTermNode(lineNumber, expr)
            }
            T_BANG -> {
                Tokens.consume(T_BANG, cursor)
                val term = term(cursor)
                return NegationNode(lineNumber, term)
            }
            T_VERTICAL_BAR -> {
                Tokens.consume(T_VERTICAL_BAR, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_VERTICAL_BAR, cursor)
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

    private fun termLookahead(cursor: Cursor): Boolean {
        val next = Tokens.nextToken(cursor, false)
        return when (next.tokenType) {
            T_ID, T_LEFT_PARENTHESIS, T_BANG, T_VERTICAL_BAR, T_NUM, T_TRUE, T_FALSE, T_NULL -> true
            else -> false
        }
    }

    private fun variable(cursor: Cursor): VariableNode {
        val access = VariableAccessNode(cursor.lineNumber, Tokens.consume(T_ID, cursor).image)
        return variableRecursion(cursor, access) ?: access
    }

    private fun variableRecursion(cursor: Cursor, variableAccessNode: VariableAccessNode): VariableNode? {
        val next = Tokens.nextToken(cursor, false)
        val lineNumber = cursor.lineNumber
        return when (next.tokenType) {
            T_LEFT_SQUARE_BRACE -> {
                Tokens.consume(T_LEFT_SQUARE_BRACE, cursor)
                val expr = expression(cursor)
                Tokens.consume(T_RIGHT_SQUARE_BRACE, cursor)
                ArrayAccessNode(lineNumber, variableAccessNode, expr)
            }
            T_DOT -> {
                Tokens.consume(T_DOT, cursor)
                val id = Tokens.consume(T_ID, cursor)
                FieldAccessNode(lineNumber, variableAccessNode, id.image)
            }
            else -> null
        }
    }
}
