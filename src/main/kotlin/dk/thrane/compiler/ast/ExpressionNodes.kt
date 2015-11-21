package dk.thrane.compiler.ast

open class ExpressionNode(override var lineNumber: Int) : Node
class BinaryExpressionNode(lineNumber: Int, var left: ExpressionNode, var right: ExpressionNode, var operator: Tokens) :
        ExpressionNode(lineNumber) {
    override val children = listOf(left, right)
}
class TermExpressionNode(lineNumber: Int, var term: TermNode) : ExpressionNode(lineNumber) {
    override val children = listOf(term)
}
