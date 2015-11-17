package dk.thrane.compiler.ast

open class ExpressionNode(override val lineNumber: Int) : Node
class BinaryExpressionNode(lineNumber: Int, val left: ExpressionNode, val right: ExpressionNode, val operator: Tokens) :
        ExpressionNode(lineNumber) {
    override val children = listOf(left, right)
}
class TermExpressionNode(lineNumber: Int, val term: TermNode) : ExpressionNode(lineNumber) {
    override val children = listOf(term)
}
