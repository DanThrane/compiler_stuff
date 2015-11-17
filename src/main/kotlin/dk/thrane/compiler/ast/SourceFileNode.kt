package dk.thrane.compiler.ast

data class SourceFileNode(override val lineNumber: Int, val functions: List<FunctionNode>) : Node {
    override val children = functions
}