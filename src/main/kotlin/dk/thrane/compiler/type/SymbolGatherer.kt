package dk.thrane.compiler.type

import dk.thrane.compiler.ast.FieldDeclarationNode
import dk.thrane.compiler.ast.FunctionNode
import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.RecordTypeNode
import dk.thrane.compiler.ast.TypeDeclarationNode
import dk.thrane.compiler.ast.VariableDeclarationNode
import dk.thrane.compiler.ast.Visitor

class SymbolGatherer : Visitor() {
    val global = GlobalScope()
    private var currentScope: Scope = global

    private fun <T : Scope> enterScope(companion: ScopeCompanion<T>): T {
        val scopeSymbolTable = currentScope.scopeSymbolTable(companion)
        currentScope = scopeSymbolTable
        return scopeSymbolTable
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
                val parameterTypes = node.head.parameters.map { Pair(it.name, it.typeNode.toNativeType()) }
                val returnType = if (node.head.typeNode != null) node.head.typeNode!!.toNativeType() else TypeUnit
                val typeFunction = TypeFunction(parameterTypes, returnType)
                currentScope.putSymbol(node.head.name, typeFunction)
                val functionScope = enterScope(FunctionScope)
                functionScope.function = typeFunction
                currentScope.putSymbol("#return", returnType)
                node.function = typeFunction
            }

            is VariableDeclarationNode -> {
                node.variables.forEach {
                    currentScope.putSymbol(it.name, it.typeNode.toNativeType())
                }
            }

            is FieldDeclarationNode -> {
                currentScope.putSymbol(node.name, node.typeNode.toNativeType())
            }

            is RecordTypeNode -> {
                enterScope(RecordScope)
            }

            else -> {
                // Do nothing
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
