package dk.thrane.compiler.ast

open class ExpressionNode(val lineNumber: Int)
class BinaryExpressionNode(lineNumber: Int, val left: ExpressionNode, val right: ExpressionNode, val operator: Tokens) :
        ExpressionNode(lineNumber)
class TermExpressionNode(lineNumber: Int, val term: TermNode) : ExpressionNode(lineNumber)
