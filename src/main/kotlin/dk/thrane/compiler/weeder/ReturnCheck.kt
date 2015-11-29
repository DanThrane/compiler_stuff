package dk.thrane.compiler.weeder

import dk.thrane.compiler.ast.*

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
                    throw IllegalStateException("Unable to find return in function starting at line: " +
                            "${node.lineNumber}")
                }
            }
        }
    }

    override fun exitNode(node: Node) {

    }
}