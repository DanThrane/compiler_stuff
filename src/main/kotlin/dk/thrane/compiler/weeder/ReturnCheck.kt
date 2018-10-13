package dk.thrane.compiler.weeder

import dk.thrane.compiler.ast.BlockNode
import dk.thrane.compiler.ast.FunctionBody
import dk.thrane.compiler.ast.FunctionNode
import dk.thrane.compiler.ast.IfNode
import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.ReturnNode
import dk.thrane.compiler.ast.StatementNode
import dk.thrane.compiler.ast.Visitor
import dk.thrane.compiler.type.TypeUnit

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
        return if (statement is BlockNode) {
            checkStatementList(statement.statements)
        } else {
            statement is ReturnNode
        }
    }

    override fun enterNode(node: Node) {
        when (node) {
            is FunctionNode -> {
                // We don't need to check functions with unit returns
                if (node.function.returnType == TypeUnit) return

                if (!checkStatementList(node.body.statements)) {
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
