package dk.thrane.compiler.ast

abstract class Visitor {
    abstract fun enterNode(node: Node)
    abstract fun exitNode(node: Node)
    var visitChildren: Boolean = true

    fun traverse(root: Node) {
        enterNode(root)
        if (visitChildren) {
            for (child in root.children) {
                traverse(child)
            }
        }
        visitChildren = true
        exitNode(root)
    }
}
