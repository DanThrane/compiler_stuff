package dk.thrane.compiler.bytecode

import java.util.*

class ClassFile {
    companion object {
        const val MAGIC = 0xCAFEBABE
        const val MAJOR_VERSION = 52
        const val MINOR_VERSION = 0
    }

    private val constantPool: MutableList<ConstantPoolEntry> = ArrayList()
    private val accessFlags: MutableList<AccessFlag> = ArrayList()
    // u2: this class
    // u2: super class
    // u2: interface count
    // u2: interfaces
    private val classFields: MutableList<ClassField> = ArrayList()
    private val methods: MutableList<Method> = ArrayList()
    private val attributes: MutableList<Attribute> = ArrayList()
}
