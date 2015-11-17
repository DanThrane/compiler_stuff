package dk.thrane.compiler.ast

data class FieldDeclarationNode(override val lineNumber: Int, val name: String, val type: TypeNode) : Node {
    override val children = listOf(type)
}

open class DeclarationNode(override val lineNumber: Int) : Node

class FunctionDeclarationNode(lineNumber: Int, val functionNode: FunctionNode) : DeclarationNode(lineNumber) {
    override val children = listOf(functionNode)
}

class TypeDeclarationNode(lineNumber: Int, val name: String, val typeNode: TypeNode) : DeclarationNode(lineNumber) {
    override val children = listOf(typeNode)
}

class VariableDeclarationNode(lineNumber: Int, val variables: List<FieldDeclarationNode>) :
        DeclarationNode(lineNumber) {
    override val children = variables
}
