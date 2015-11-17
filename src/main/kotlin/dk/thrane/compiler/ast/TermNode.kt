package dk.thrane.compiler.ast

open class TermNode(val lineNumber: Int)
class VariableTermNode(lineNumber: Int, val variableNode: VariableNode) : TermNode(lineNumber)
class FunctionCallNode(lineNumber: Int, val name: String, val arguments: List<ExpressionNode>) : TermNode(lineNumber)
class ParenthesisTermNode(lineNumber: Int, val expressionNode: ExpressionNode) : TermNode(lineNumber)
class NegationNode(lineNumber: Int, val termNode: TermNode) : TermNode(lineNumber)
class AbsoluteNode(lineNumber: Int, val expressionNode: ExpressionNode) : TermNode(lineNumber)
class NumberLiteralNode(lineNumber: Int, val value: Int) : TermNode(lineNumber)
class BooleanLiteralNode(lineNumber: Int, val value: Boolean) : TermNode(lineNumber)
class NullLiteralNode(lineNumber: Int) : TermNode(lineNumber)
class StringLiteralNode(lineNumber: Int, val value: String) : TermNode(lineNumber)
class CharacterLiteralNode(lineNumber: Int, val value: Char) : TermNode(lineNumber)