package dk.thrane.compiler.ast

import dk.thrane.compiler.type.SymbolTable
import dk.thrane.compiler.type.Type
import dk.thrane.compiler.type.TypeArray
import dk.thrane.compiler.type.TypeBool
import dk.thrane.compiler.type.TypeChar
import dk.thrane.compiler.type.TypeFunction
import dk.thrane.compiler.type.TypeInt
import dk.thrane.compiler.type.TypeRecord
import dk.thrane.compiler.type.TypeUnresolved

sealed class Node {
    abstract val lineNumber: Int
    var scope: SymbolTable? = null
    var type: Type? = null
    var function: TypeFunction? = null
    open val children: List<Node>
        get() = emptyList()
}

// Expressions

sealed class ExpressionNode(override var lineNumber: Int) : Node()

class BinaryExpressionNode(
    lineNumber: Int,
    var left: ExpressionNode,
    var right: ExpressionNode,
    var operator: Operator
) :
    ExpressionNode(lineNumber) {
    override val children = listOf(left, right)
}

class TermExpressionNode(lineNumber: Int, var term: TermNode) : ExpressionNode(lineNumber) {
    override val children = listOf(term)
}

// Declarations

class FieldDeclarationNode(override var lineNumber: Int, var name: String, var typeNode: TypeNode) : Node() {
    override val children = listOf(typeNode)
}

sealed class DeclarationNode(override var lineNumber: Int) : Node()

class TypeDeclarationNode(lineNumber: Int, var name: String, var typeNode: TypeNode) : DeclarationNode(lineNumber) {
    override val children = listOf(typeNode)
}

class VariableDeclarationNode(lineNumber: Int, var variables: MutableList<FieldDeclarationNode>) :
    DeclarationNode(lineNumber) {
    override val children = variables
}

// Functions

class FunctionNode(override var lineNumber: Int, var head: FunctionHead, var body: FunctionBody,
                   var tail: FunctionTail) : Node() {
    override val children: List<Node>
        get() = listOf(head, body, tail)
}

class FunctionHead(override var lineNumber: Int, var name: String,
                   var parameters: MutableList<FieldDeclarationNode>, val typeNode: TypeNode?) : Node() {
    override val children: List<Node>
        get() = parameters
}
class FunctionBody(override var lineNumber: Int, var declarations: List<DeclarationNode>,
                   var statements: MutableList<StatementNode>) : Node() {
    override val children: List<Node>
        get() = declarations + statements
}

class FunctionTail(override var lineNumber: Int, var name: String) : Node()

// Other

class SourceFileNode(override val lineNumber: Int, val functions: List<FunctionNode>) : Node() {
    override val children = functions
}

// Statements

sealed class StatementNode(override var lineNumber: Int) : Node()

class ReturnNode(lineNumber: Int, var expression: ExpressionNode) : StatementNode(lineNumber) {
    override val children = listOf(expression)
}
class WriteNode(lineNumber: Int, var expression: ExpressionNode) : StatementNode(lineNumber) {
    override val children = listOf(expression)
}

class AllocNode(lineNumber: Int, var variable: VariableNode, var expression: ExpressionNode?) :
    StatementNode(lineNumber) {
    override val children = listOf(variable) + (if (expression != null) listOf(expression as Node) else emptyList())
}

class AssignmentNode(lineNumber: Int, var variable: VariableNode, var expression: ExpressionNode) :
    StatementNode(lineNumber) {
    override val children = listOf(variable, expression)
}

class IfNode(lineNumber: Int, var expression: ExpressionNode, var statementNode: StatementNode,
             var elseNode: StatementNode?) : StatementNode(lineNumber) {
    override val children: List<Node>
        get() = listOf(expression, statementNode) + (if (elseNode != null) listOf(elseNode as Node) else emptyList())
}

class WhileNode(lineNumber: Int, var expression: ExpressionNode, statementNode: StatementNode) :
    StatementNode(lineNumber) {
    override val children = listOf(expression, statementNode)
}

class BlockNode(lineNumber: Int, var statements: MutableList<StatementNode>) : StatementNode(lineNumber) {
    override val children = statements
}

// Types

sealed class TypeNode(override var lineNumber: Int, var token: Tokens) : Node() {
    override fun toString(): String {
        return type.toString()
    }

    open fun toNativeType(): Type {
        return when (token) {
            Tokens.T_INT -> TypeInt()
            Tokens.T_BOOL -> TypeBool()
            Tokens.T_CHAR -> TypeChar()
            else -> throw IllegalStateException("Unknown token type ($type)!")
        }
    }
}

class PrimitiveTypeNode(lineNumber: Int, token: Tokens) : TypeNode(lineNumber, token)

class IdentifierType(lineNumber: Int, val identifier: String) : TypeNode(lineNumber, Tokens.T_ID) {
    override fun toNativeType(): Type = TypeUnresolved(identifier)
}

class RecordTypeNode(lineNumber: Int, var fields: MutableList<FieldDeclarationNode>) : TypeNode(lineNumber, Tokens.T_RECORD) {
    override fun toNativeType(): Type = TypeRecord(fields.map { Pair(it.name, it.typeNode.toNativeType()) })

    override val children = fields

    override fun toString(): String {
        val builder = StringBuilder("record of (")
        var first = true
        for (field in fields) {
            if (!first) builder.append(", ")
            else first = true

            builder.append("${field.name}: ${field.type}")
        }
        builder.append(")")
        return builder.toString()
    }
}

class ArrayTypeNode(lineNumber: Int, var arrayType: TypeNode) : TypeNode(lineNumber, Tokens.T_ARRAY) {
    override fun toNativeType(): Type = TypeArray(arrayType.toNativeType())

    override val children = listOf(arrayType)

    override fun toString(): String {
        return "array of $arrayType"
    }
}

// Variables

sealed class VariableNode(override var lineNumber: Int) : Node()

class VariableAccessNode(lineNumber: Int, var identifier: String) : VariableNode(lineNumber)

class ArrayAccessNode(lineNumber: Int, var variableAccessNode: VariableAccessNode, var expressionNode: ExpressionNode):
    VariableNode(lineNumber) {
    override val children = listOf(variableAccessNode, expressionNode)
}

class FieldAccessNode(lineNumber: Int, var variableAccessNode: VariableAccessNode, var fieldName: String) :
    VariableNode(lineNumber) {
    override val children = listOf(variableAccessNode)
}

// Terms

sealed class TermNode(override var lineNumber: Int) : Node()

class VariableTermNode(lineNumber: Int, var variableNode: VariableNode) : TermNode(lineNumber) {
    override val children = listOf(variableNode)
}

class FunctionCallNode(lineNumber: Int, var name: String, var arguments: MutableList<ExpressionNode>) :
    TermNode(lineNumber) {
    override val children = arguments
}

class ParenthesisTermNode(lineNumber: Int, var expressionNode: ExpressionNode) : TermNode(lineNumber) {
    override val children = listOf(expressionNode)
}

class NegationNode(lineNumber: Int, var termNode: TermNode) : TermNode(lineNumber) {
    override val children = listOf(termNode)
}

class AbsoluteNode(lineNumber: Int, var expressionNode: ExpressionNode) : TermNode(lineNumber) {
    override val children = listOf(expressionNode)
}

class NumberLiteralNode(lineNumber: Int, var value: Int) : TermNode(lineNumber)

class BooleanLiteralNode(lineNumber: Int, var value: Boolean) : TermNode(lineNumber)

class NullLiteralNode(lineNumber: Int) : TermNode(lineNumber)

class StringLiteralNode(lineNumber: Int, var value: String) : TermNode(lineNumber)

class CharacterLiteralNode(lineNumber: Int, var value: Char) : TermNode(lineNumber)
