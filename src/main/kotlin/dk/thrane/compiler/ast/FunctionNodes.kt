package dk.thrane.compiler.ast

class FunctionNode(override var lineNumber: Int, var head: FunctionHead, var body: FunctionBody,
                        var tail: FunctionTail) : Node() {
    override val children: List<Node>
        get() = listOf(head, body, tail)
}

class FunctionHead(override var lineNumber: Int, var name: String,
                        var parameters: MutableList<FieldDeclarationNode>, val typeNode: TypeNode?) : Node() {
    override val children: List<Node>
        get() = parameters
}
class FunctionBody(override var lineNumber: Int, var declarations: List<DeclarationNode>,
                        var statements: MutableList<StatementNode>) : Node() {
    override val children: List<Node>
        get() = declarations + statements
}

class FunctionTail(override var lineNumber: Int, var name: String) : Node()
