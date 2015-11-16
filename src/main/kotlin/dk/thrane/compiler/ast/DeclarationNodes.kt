package dk.thrane.compiler.ast

data class FieldDeclarationNode(val lineNumber: Int, val name: String, val type: TypeNode)

open class DeclarationNode(val lineNumber: Int)
class FunctionDeclarationNode(lineNumber: Int, val functionNode: FunctionNode) : DeclarationNode(lineNumber)
class TypeDeclarationNode(lineNumber: Int, val name: String, val typeNode: TypeNode) : DeclarationNode(lineNumber)
class VariableDeclarationNode(lineNumber: Int, val variables: List<FieldDeclarationNode>) :
        DeclarationNode(lineNumber)
