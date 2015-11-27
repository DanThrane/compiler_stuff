package dk.thrane.compiler.type

import dk.thrane.compiler.ast.*

class SymbolGatherer : Visitor() {
    val global: SymbolTable = SymbolTable()
    private var currentScope: SymbolTable = global

    private fun enterScope() {
        currentScope = currentScope.scopeSymbolTable()
    }

    private fun exitScope() {
        currentScope = currentScope.parent!!
    }

    override fun enterNode(node: Node) {
        node.scope = currentScope
        when (node) {
            is TypeDeclarationNode -> {
                currentScope.putSymbol(node.name, Type.fromASTType(node.typeNode))
            }
            is FunctionNode -> {
                val type = if (node.head.type != null) Type.fromASTType(node.head.type!!)
                           else TypeUnit()
                currentScope.putSymbol(node.head.name, type)
                enterScope()
            }
            is VariableDeclarationNode -> {
                node.variables.forEach {
                    currentScope.putSymbol(it.name, Type.fromASTType(it.type))
                }
            }
            is FieldDeclarationNode -> {
                currentScope.putSymbol(node.name, Type.fromASTType(node.type))
            }
            is RecordTypeNode -> {
                println("Entering")
                enterScope()
            }
        }
    }

    override fun exitNode(node: Node) {
        when (node) {
            is FunctionNode, is RecordTypeNode -> {
                println("Exiting")
                exitScope()
            }
        }
    }
}