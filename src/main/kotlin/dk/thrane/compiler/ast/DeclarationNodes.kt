package dk.thrane.compiler.ast

class FieldDeclarationNode(override var lineNumber: Int, var name: String, var typeNode: TypeNode) : Node() {
    override val children = listOf(typeNode)
}

open class DeclarationNode(override var lineNumber: Int) : Node()

class TypeDeclarationNode(lineNumber: Int, var name: String, var typeNode: TypeNode) : DeclarationNode(lineNumber) {
    override val children = listOf(typeNode)
}

class VariableDeclarationNode(lineNumber: Int, var variables: MutableList<FieldDeclarationNode>) :
        DeclarationNode(lineNumber) {
    override val children = variables
}
