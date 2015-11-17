package dk.thrane.compiler.ast

data class FunctionNode(val lineNumber: Int, val head: FunctionHead, val body: FunctionBody, val tail: FunctionTail)
data class FunctionHead(val lineNumber: Int, val name: String, val parameters: List<FieldDeclarationNode>)
data class FunctionBody(val lineNumber: Int, val declarations: List<DeclarationNode>,
                        val statements: List<StatementNode>)
data class FunctionTail(val lineNumber: Int, val name: String)
