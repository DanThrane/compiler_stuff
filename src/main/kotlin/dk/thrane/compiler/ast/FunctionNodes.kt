package dk.thrane.compiler.ast

data class FunctionNode(override val lineNumber: Int, val head: FunctionHead, val body: FunctionBody,
                        val tail: FunctionTail) : Node {
    override val children = listOf(head, body, tail)
}

data class FunctionHead(override val lineNumber: Int, val name: String,
                        val parameters: List<FieldDeclarationNode>) : Node {
    override val children = parameters
}
data class FunctionBody(override val lineNumber: Int, val declarations: List<DeclarationNode>,
                        val statements: List<StatementNode>) : Node {
    override val children = statements
}

data class FunctionTail(override val lineNumber: Int, val name: String) : Node
