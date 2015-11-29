package dk.thrane.compiler.bytecode

interface ReturnDescriptor
interface FieldType : ReturnDescriptor

enum class BaseType(val term: String) : FieldType {
    BYTE("B"),
    CHAR("C"),
    DOUBLE("D"),
    FLOAT("F"),
    INT("I"),
    LONG("J"),
    SHORT("S"),
    BOOLEAN("Z");

    override fun toString(): String = term
}

class ObjectType(val className: String) : FieldType {
    override fun toString(): String = "L$className;"
}

class ArrayType(val type: FieldType) : FieldType {
    override fun toString(): String = "[$type"
}

class VoidDescriptor : ReturnDescriptor {
    override fun toString(): String = "V"
}

class MethodDescriptor(val parameterTypes: List<FieldType>, val returnType: ReturnDescriptor) {
    override fun toString(): String = "(${parameterTypes.map { it.toString() }.joinToString("")})$returnType"
}
