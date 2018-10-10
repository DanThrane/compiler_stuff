package dk.thrane.compiler.type

import dk.thrane.compiler.ast.FieldDeclarationNode
import dk.thrane.compiler.ast.FunctionNode
import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.RecordTypeNode
import dk.thrane.compiler.ast.TypeDeclarationNode
import dk.thrane.compiler.ast.VariableDeclarationNode
import dk.thrane.compiler.ast.Visitor

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
                val parameterTypes = node.head.parameters.map { Pair(it.name, it.typeNode.toNativeType()) }
                val returnType = if (node.head.typeNode != null) node.head.typeNode!!.toNativeType() else TypeUnit()
                val typeFunction = TypeFunction(parameterTypes, returnType)
                currentScope.putSymbol(node.head.name, typeFunction)
                enterScope()
                currentScope.function = typeFunction
                currentScope.putSymbol("#return", returnType)
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
                enterScope()
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
