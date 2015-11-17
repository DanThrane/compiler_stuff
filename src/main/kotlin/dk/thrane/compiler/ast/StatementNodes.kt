package dk.thrane.compiler.ast

open class StatementNode(override val lineNumber: Int) : Node

class ReturnNode(lineNumber: Int, val expression: ExpressionNode) : StatementNode(lineNumber) {
    override val children = listOf(expression)
}
class WriteNode(lineNumber: Int, val expression: ExpressionNode) : StatementNode(lineNumber) {
    override val children = listOf(expression)
}

class AllocNode(lineNumber: Int, val variable: VariableNode, val expression: ExpressionNode?) :
        StatementNode(lineNumber) {
    override val children = listOf(variable) + (if (expression != null) listOf(expression as Node) else emptyList())
}

class AssignmentNode(lineNumber: Int, val variable: VariableNode, val expression: ExpressionNode) :
        StatementNode(lineNumber) {
    override val children = listOf(variable, expression)
}

class IfNode(lineNumber: Int, val expression: ExpressionNode, val statementNode: StatementNode,
             val elseNode: StatementNode?) : StatementNode(lineNumber) {
    override val children: List<Node>
        get() = listOf(expression, statementNode) + (if (elseNode != null) listOf(elseNode as Node) else emptyList())
}

class WhileNode(lineNumber: Int, val expression: ExpressionNode, val statementNode: StatementNode) :
        StatementNode(lineNumber) {
    override val children = listOf(expression, statementNode)
}

class BlockNode(lineNumber: Int, val statements: List<StatementNode>) : StatementNode(lineNumber) {
    override val children = statements
}
