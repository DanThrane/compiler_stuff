package dk.thrane.compiler.ast

import dk.thrane.compiler.type.*

open class TypeNode(override var lineNumber: Int, var token: Tokens) : Node() {
    override fun toString(): String {
        return type.toString()
    }

    open fun toNativeType(): Type {
        when (token) {
            Tokens.T_INT -> return TypeInt()
            Tokens.T_BOOL -> return TypeBool()
            Tokens.T_CHAR -> return TypeChar()
            else -> throw IllegalStateException("Unknown token type ($type)!")
        }
    }
}

class IdentifierType(lineNumber: Int, val identifier: String) : TypeNode(lineNumber, Tokens.T_ID) {
    override fun toNativeType(): Type = TypeUnresolved(identifier)
}

class RecordTypeNode(lineNumber: Int, var fields: MutableList<FieldDeclarationNode>) : TypeNode(lineNumber, Tokens.T_RECORD) {
    override fun toNativeType(): Type = TypeRecord(fields.map { Pair(it.name, it.typeNode.toNativeType()) })

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
    override fun toNativeType(): Type = TypeArray(arrayType.toNativeType())

    override val children = listOf(arrayType)

    override fun toString(): String {
        return "array of $arrayType"
    }
}
