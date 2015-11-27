package dk.thrane.compiler.ast

open class VariableNode(override var lineNumber: Int) : Node()

class VariableAccessNode(lineNumber: Int, var identifier: String) : VariableNode(lineNumber)

class ArrayAccessNode(lineNumber: Int, var variableAccessNode: VariableAccessNode, var expressionNode: ExpressionNode):
        VariableNode(lineNumber) {
    override val children = listOf(variableAccessNode, expressionNode)
}

class FieldAccessNode(lineNumber: Int, var variableAccessNode: VariableAccessNode, var fieldName: String) :
        VariableNode(lineNumber) {
    override val children = listOf(variableAccessNode)
}