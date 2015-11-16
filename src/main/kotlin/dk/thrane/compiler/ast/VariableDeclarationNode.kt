package dk.thrane.compiler.ast

data class VariableDeclarationNode(val lineNumber: Int, val name: String, val type: TypeNode)