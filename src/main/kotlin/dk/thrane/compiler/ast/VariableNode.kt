package dk.thrane.compiler.ast

open class VariableNode(val lineNumber: Int)
class VariableAccessNode(lineNumber: Int, val identifier: String) : VariableNode(lineNumber)
class ArrayAccessNode(lineNumber: Int, val variableAccessNode: VariableAccessNode, val expressionNode: ExpressionNode):
        VariableNode(lineNumber)
class FieldAccessNode(lineNumber: Int, val variableAccessNode: VariableAccessNode, val fieldName: String) :
        VariableNode(lineNumber)