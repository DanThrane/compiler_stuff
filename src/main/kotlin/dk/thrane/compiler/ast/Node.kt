package dk.thrane.compiler.ast

interface Node {
    val lineNumber: Int
    val children: List<Node>
        get() = emptyList()
}