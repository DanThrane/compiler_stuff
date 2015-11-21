package dk.thrane.compiler.ast

open class TypeNode(override var lineNumber: Int, var type: Tokens) : Node {
    override fun toString(): String {
        return type.toString()
    }
}

class RecordTypeNode(lineNumber: Int, var fields: MutableList<FieldDeclarationNode>) : TypeNode(lineNumber, Tokens.T_RECORD) {
    override val children = fields

    override fun toString(): String {
        var builder = StringBuilder("record of (")
        var first = true
        for (field in fields) {
            if (!first) builder.append(", ")
            else first = true

            builder.append("${field.name}: ${field.type}")
        }
        builder.append(")")
        return builder.toString()
    }
}

class ArrayTypeNode(lineNumber: Int, var arrayType: TypeNode) : TypeNode(lineNumber, Tokens.T_ARRAY) {
    override val children = listOf(arrayType)

    override fun toString(): String {
        return "array of $arrayType"
    }
}
