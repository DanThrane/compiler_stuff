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
                currentScope.putSymbol(node.name, node.typeNode.toNativeType())
            }
            is FunctionNode -> {
                val parameterTypes = node.head.parameters.map { Pair(it.name, it.type.toNativeType()) }
                val returnType = if (node.head.type != null) node.head.type!!.toNativeType() else TypeUnit()
                currentScope.putSymbol(node.head.name, TypeFunction(parameterTypes, returnType))
                enterScope()
                currentScope.putSymbol("#return", returnType)
            }
            is VariableDeclarationNode -> {
                node.variables.forEach {
                    currentScope.putSymbol(it.name, it.type.toNativeType())
                }
            }
            is FieldDeclarationNode -> {
                currentScope.putSymbol(node.name, node.type.toNativeType())
            }
            is RecordTypeNode -> {
                enterScope()
            }
        }
    }

    override fun exitNode(node: Node) {
        when (node) {
            is FunctionNode, is RecordTypeNode -> {
                exitScope()
            }
        }
    }
}