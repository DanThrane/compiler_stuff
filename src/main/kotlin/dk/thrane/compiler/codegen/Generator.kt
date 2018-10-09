package dk.thrane.compiler.codegen

import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.StatementNode
import dk.thrane.compiler.ast.Visitor
import dk.thrane.compiler.ast.WriteNode

class Generator : Visitor() {
    override fun enterNode(node: Node) {
        when (node) {
            is StatementNode -> {
                handleStatements(node)
            }
        }
    }

    private fun handleStatements(node: StatementNode) {
        when (node) {
            is WriteNode -> {
                // Write some C-code
            }
        }
    }

    override fun exitNode(node: Node) {

    }
}
