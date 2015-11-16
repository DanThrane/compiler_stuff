package dk.thrane.compiler.ast

data class Function(val lineNumber: Int, val head: FunctionHead, val body: FunctionBody, val tail: FunctionTail)
data class FunctionHead(val lineNumber: Int, val name: String, val parameters: List<VariableDeclarationNode>)
data class FunctionBody(val lineNumber: Int)
data class FunctionTail(val lineNumber: Int, val name: String)
