package dk.thrane.compiler.ast

open class ExpressionNode(override var lineNumber: Int) : Node
abstract class BinaryExpressionNode(lineNumber: Int, var left: ExpressionNode, var right: ExpressionNode, var operator: Tokens) :
        ExpressionNode(lineNumber) {
    override val children = listOf(left, right)
}
class CompareEqualNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_COMPARE_EQUALS)
class CompareNotEqualNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_COMPARE_NOT_EQUALS)
class CompareLessThanNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_COMPARE_NOT_EQUALS)
class CompareGreaterThanNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_COMPARE_GREATER_THAN)
class CompareLessThanOrEqualsNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_COMPARE_LESS_THAN_OR_EQUALS)
class CompareGreaterThanOrEqualsNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_COMPARE_GREATER_THAN_OR_EQUALS)
class OrNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_OR)
class AndNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_AND)
class AddNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_ADD)
class MinusNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_SUBTRACTION)
class DivisionNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_DIVISION)
class MultiplicationNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_MULTIPLICATION)
class ModuloNode(lineNumber: Int, left: ExpressionNode, right: ExpressionNode) : BinaryExpressionNode(lineNumber, left, right, Tokens.T_PERCENT_SIGN)

class TermExpressionNode(lineNumber: Int, var term: TermNode) : ExpressionNode(lineNumber) {
    override val children = listOf(term)
}
