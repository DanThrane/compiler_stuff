package dk.thrane.compiler.ast

import dk.thrane.compiler.type.SymbolTable

abstract class Node {
    abstract val lineNumber: Int
    var scope: SymbolTable? = null
    open val children: List<Node>
        get() = emptyList()
}