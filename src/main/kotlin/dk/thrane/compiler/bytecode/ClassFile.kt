package dk.thrane.compiler.bytecode

import java.io.DataOutputStream
import java.util.*

class ClassFile {
    companion object {
        const val MAGIC = 0xCAFEBABE.toInt()
        const val MAJOR_VERSION = 49
        const val MINOR_VERSION = 0
    }

    val constantPool = ConstantPool()
    val accessFlags: MutableList<AccessFlag> = ArrayList()
    var thisClass: ConstantClassInfo? = null
    var superClass: ConstantClassInfo? = null
    var interfaces: MutableList<ConstantClassInfo> = ArrayList()
    val classFields: MutableList<ClassField> = ArrayList()
    val methods: MutableList<Method> = ArrayList()
    val attributes: MutableList<Attribute> = ArrayList()

    fun write(out: DataOutputStream) {
        out.writeInt(MAGIC)
        out.writeShort(MAJOR_VERSION)
        out.writeShort(MINOR_VERSION)
        constantPool.write(out)
        AccessFlag.combine(accessFlags)
        out.writeShort(thisClass!!.index)
        out.writeShort(if (superClass != null) superClass!!.index else 0)
        out.writeShort(interfaces.size)
        interfaces.forEach { out.writeShort(it.index) }
        out.writeShort(classFields.size)
        classFields.forEach { it.write(out) }
        out.writeShort(methods.size)
        methods.forEach { it.write(out) }
        out.writeShort(attributes.size)
        attributes.forEach { it.write(out) }
    }
}
