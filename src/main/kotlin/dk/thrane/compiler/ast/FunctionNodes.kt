package dk.thrane.compiler.ast

data class FunctionNode(override var lineNumber: Int, var head: FunctionHead, var body: FunctionBody,
                        var tail: FunctionTail) : Node {
    override val children: List<Node>
        get() = listOf(head, body, tail)
}

data class FunctionHead(override var lineNumber: Int, var name: String,
                        var parameters: List<FieldDeclarationNode>) : Node {
    override val children: List<Node>
        get() = parameters
}
data class FunctionBody(override var lineNumber: Int, var declarations: List<DeclarationNode>,
                        var statements: List<StatementNode>) : Node {
    override val children: List<Node>
        get() = statements
}

data class FunctionTail(override var lineNumber: Int, var name: String) : Node
