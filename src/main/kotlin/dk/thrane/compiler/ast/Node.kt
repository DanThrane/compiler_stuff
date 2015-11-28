package dk.thrane.compiler.ast

import dk.thrane.compiler.type.SymbolTable
import dk.thrane.compiler.type.Type
import dk.thrane.compiler.type.TypeFunction

abstract class Node {
    abstract val lineNumber: Int
    var scope: SymbolTable? = null
    var type: Type? = null
    var function: TypeFunction? = null
    open val children: List<Node>
        get() = emptyList()
}