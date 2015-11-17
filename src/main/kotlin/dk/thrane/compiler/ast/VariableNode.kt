package dk.thrane.compiler.ast

open class VariableNode(override val lineNumber: Int) : Node

class VariableAccessNode(lineNumber: Int, val identifier: String) : VariableNode(lineNumber)

class ArrayAccessNode(lineNumber: Int, val variableAccessNode: VariableAccessNode, val expressionNode: ExpressionNode):
        VariableNode(lineNumber) {
    override val children = listOf(variableAccessNode, expressionNode)
}

class FieldAccessNode(lineNumber: Int, val variableAccessNode: VariableAccessNode, val fieldName: String) :
        VariableNode(lineNumber) {
    override val children = listOf(variableAccessNode)
}