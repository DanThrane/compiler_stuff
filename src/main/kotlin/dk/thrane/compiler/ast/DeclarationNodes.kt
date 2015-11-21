package dk.thrane.compiler.ast

data class FieldDeclarationNode(override var lineNumber: Int, var name: String, var type: TypeNode) : Node {
    override val children = listOf(type)
}

open class DeclarationNode(override var lineNumber: Int) : Node

class FunctionDeclarationNode(lineNumber: Int, var functionNode: FunctionNode) : DeclarationNode(lineNumber) {
    override val children = listOf(functionNode)
}

class TypeDeclarationNode(lineNumber: Int, var name: String, var typeNode: TypeNode) : DeclarationNode(lineNumber) {
    override val children = listOf(typeNode)
}

class VariableDeclarationNode(lineNumber: Int, var variables: MutableList<FieldDeclarationNode>) :
        DeclarationNode(lineNumber) {
    override val children = variables
}
