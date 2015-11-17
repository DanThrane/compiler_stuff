package dk.thrane.compiler.ast

open class VariableNode(val lineNumber: Int)
class ArrayAccessNode(lineNumber: Int, val identifier: String, val expressionNode: ExpressionNode):
        VariableNode(lineNumber)
class FieldAccessNode(lineNumber: Int, val identifier: String, val fieldName: String) : VariableNode(lineNumber)