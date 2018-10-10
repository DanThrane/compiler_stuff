package dk.thrane.compiler.ast

class FunctionCheck : Visitor() {
    override fun enterNode(node: Node) {
        when (node) {
            is FunctionNode -> {
                if (node.head.name != node.tail.name) {
                    throw IllegalStateException(
                        "Name in head of function does not match name in tail of function!" +
                                " Head defined at line ${node.head.lineNumber}, tail at line ${node.tail.lineNumber}"
                    )
                }
                visitChildren = false
            }

            else -> {
                // Do nothing
            }
        }
    }

    override fun exitNode(node: Node) {
    }
}
