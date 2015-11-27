package dk.thrane.compiler.ast

open class TermNode(override var lineNumber: Int) : Node()

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