package dk.thrane.compiler.weeder

import dk.thrane.compiler.ast.BlockNode
import dk.thrane.compiler.ast.FunctionBody
import dk.thrane.compiler.ast.IfNode
import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.ReturnNode
import dk.thrane.compiler.ast.StatementNode
import dk.thrane.compiler.ast.Visitor

class ReturnCheck : Visitor() {
    private fun checkStatementList(statements: List<StatementNode>): Boolean {
        return statements.any {
            if (it is IfNode) {
                if (it.elseNode != null) {
                    checkBlock(it.statementNode) && checkBlock(it.elseNode!!)
                } else {
                    false
                }
            } else {
                checkBlock(it)
            }
        }
    }

    private fun checkBlock(statement: StatementNode): Boolean {
        if (statement is BlockNode) {
            return checkStatementList(statement.statements)
        } else {
            return statement is ReturnNode
        }
    }

    override fun enterNode(node: Node) {
        when (node) {
            is FunctionBody -> {
                if (!checkStatementList(node.statements)) {
                    throw IllegalStateException(
                        "Unable to find return in function starting at line: " +
                                "${node.lineNumber}"
                    )
                }
            }

            else -> {
                // Do nothing
            }
        }
    }

    override fun exitNode(node: Node) {

    }
}
