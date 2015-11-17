package dk.thrane.compiler.ast

open class StatementNode(val lineNumber: Int)
class ReturnNode(lineNumber: Int, val expression: ExpressionNode) : StatementNode(lineNumber)
class WriteNode(lineNumber: Int, val expression: ExpressionNode) : StatementNode(lineNumber)
class AllocNode(lineNumber: Int, val variable: VariableNode, val expression: ExpressionNode?) : StatementNode(lineNumber)
class AssignmentNode(lineNumber: Int, val variable: VariableNode, val expression: ExpressionNode) : StatementNode(lineNumber)
class IfNode(lineNumber: Int, val expression: ExpressionNode, val statementNode: StatementNode, val elseNode: StatementNode?) : StatementNode(lineNumber)
class WhileNode(lineNumber: Int, val expression: ExpressionNode, val statementNode: StatementNode) : StatementNode(lineNumber)
class BlockNode(lineNumber: Int, val statements: List<StatementNode>) : StatementNode(lineNumber)
