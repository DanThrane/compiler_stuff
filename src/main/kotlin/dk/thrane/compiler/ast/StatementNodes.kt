package dk.thrane.compiler.ast

open class StatementNode(override var lineNumber: Int) : Node()

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

class WhileNode(lineNumber: Int, var expression: ExpressionNode, var statementNode: StatementNode) :
        StatementNode(lineNumber) {
    override val children = listOf(expression, statementNode)
}

class BlockNode(lineNumber: Int, var statements: MutableList<StatementNode>) : StatementNode(lineNumber) {
    override val children = statements
}
