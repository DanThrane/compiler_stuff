package dk.thrane.compiler.bytecode

interface Descriptor
interface ReturnDescriptor : Descriptor
interface FieldDescriptor : ReturnDescriptor

enum class BaseDescriptor(val term: String) : FieldDescriptor {
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

class ObjectDescriptor(val className: String) : FieldDescriptor {
    constructor(vararg classes: String) : this(classes.joinToString("/")) {}

    override fun toString(): String = "L$className;"
}

class ArrayDescriptor(val type: FieldDescriptor) : FieldDescriptor {
    override fun toString(): String = "[$type"
}

object VoidDescriptor : ReturnDescriptor {
    override fun toString(): String = "V"
}

class MethodDescriptor(val parameterTypes: List<FieldDescriptor>, val returnType: ReturnDescriptor) : Descriptor {
    override fun toString(): String = "(${parameterTypes.map { it.toString() }.joinToString("")})$returnType"
}
