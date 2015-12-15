package dk.thrane.compiler.bytecode

import java.io.DataOutputStream
import java.nio.channels.Channels
import java.nio.charset.Charset
import java.util.*

class ConstantPool {
    private val entries: MutableList<ConstantPoolEntry> = ArrayList()
    private var nextIndex: Int = 0
    private val strings: MutableMap<String, ConstantUtf8Info> = HashMap()

    fun write(out: DataOutputStream) {
        out.writeShort(nextIndex)
        entries.forEach { it.write(out) }
    }

    fun insertEntry(entry: ConstantPoolEntry) {
        entry.index = nextIndex
        entry.pool = this
        entries.add(entry)
        nextIndex += entry.entries
    }

    fun insertString(string: String): ConstantUtf8Info {
        if (strings.contains(string)) {
            return strings[string] ?: throw IllegalStateException("String disappeared unexpectedly.")
        }
        val constantUtfInfo = ConstantUtf8Info(string)
        insertEntry(constantUtfInfo)
        return constantUtfInfo
    }
}

/**
 * Represents a single entry in the constant pool
 */
abstract class ConstantPoolEntry(val tag: Int, val entries: Int = 1) {
    var index: Int = -1
    var pool: ConstantPool? = null

    fun write(out: DataOutputStream) {
        out.writeByte(tag)
        writeBytes(out)
    }

    open fun writeBytes(out: DataOutputStream) {}
}

/**
 * Represents a class or an interface. The name index must be a valid index into the constant pool table, pointing to a
 * [ConstantUtf8Info] structure, which should represent a valid binary class or interface encoded in the internal form
 * (§4.2.1)
 */
class ConstantClassInfo(val name: ConstantUtf8Info) : ConstantPoolEntry(7) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeShort(name.index)
    }
}

/**
 * Represents a reference to a field, method, or a reference.
 *
 * The value of the [classEntry] item must be a valid index into the constant_pool table. The  constant_pool entry at
 * that index must be a [ConstantClassInfo] structure (§4.4.1) representing a class or interface type that has the
 * field or method as a member.
 *
 * The value of the [nameAndType] item must be a valid index into the constant pool table. The [ConstantPoolEntry]
 * entry at that index must be a [ConstantNameAndInfoType] structure (§4.4.6). This  constant_pool entry indicates the
 * name and descriptor of the field or method.
 *
 * In a [ConstantFieldRefInfo], the indicated descriptor must be a field descriptor (§4.3.2). Otherwise, the
 * indicated descriptor must be a method descriptor (§4.3.3).
 *
 * If the name of the method of a [ConstantMethodRefInfo] structure begins with a ' < ' (' \u003c '), then the name
 * must be the special name  <init> , representing an instance initialization method (§2.9). The return type of such a
 * method must be void.
 */
abstract class ConstantRefInfo(tag: Int, val classEntry: ConstantClassInfo,
                               val nameAndType: ConstantNameAndInfoType) : ConstantPoolEntry(tag) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeShort(classEntry.index)
        out.writeShort(nameAndType.index)
    }
}

class ConstantFieldRefInfo(classIndex: ConstantClassInfo, nameAndTypeIndex: ConstantNameAndInfoType) :
        ConstantRefInfo(9, classIndex, nameAndTypeIndex)

class ConstantMethodRefInfo(classIndex: ConstantClassInfo, nameAndTypeIndex: ConstantNameAndInfoType) :
        ConstantRefInfo(10, classIndex, nameAndTypeIndex)

class ConstantInterfaceMethodRefInfo(classIndex: ConstantClassInfo, nameAndTypeIndex: ConstantNameAndInfoType) :
        ConstantRefInfo(11, classIndex, nameAndTypeIndex)

/**
 * Used to represent constant objects of the type String
 *
 * The value of the [string] item must be a valid index into the constant pool table. The [ConstantPoolEntry] at
 * that index must be a [ConstantUtf8Info] structure (§4.4.7) representing the sequence of Unicode code points to
 * which the String object is to be initialized.
 */
class ConstantStringInfo(val string: ConstantUtf8Info) : ConstantPoolEntry(8) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeShort(string.index)
    }
}

abstract class Constant4ByteNumericInfo(tag: Int) : ConstantPoolEntry(tag)

/**
 * The bytes item of the [ConstantIntegerInfo] structure represents the value of the  int constant. The bytes of the
 * value are stored in big-endian (high byte first) order.
 */
class ConstantIntegerInfo(val value: Int) : Constant4ByteNumericInfo(3) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeInt(value)
    }
}

/**
 * The bytes item of the [ConstantFloatInfo] structure represents the value of the float constant in IEEE 754
 * floating-point single format (§2.3.2). The bytes of the single format representation are stored in big-endian
 * (high byte first) order.
 */
class ConstantFloatInfo(val value: Float) : Constant4ByteNumericInfo(4) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeFloat(value)
    }
}

/**
 * #IMPORTANT:
 * All 8-byte constants take up two entries in the constant_pool table of the class file. If a
 * [Constant8ByteNumericInfo] is the item in the constant pool table at index n, then the next usable item in
 * the pool is located at index n + 2. The  constant pool index n + 1 must be valid but is considered unusable.
 */
abstract class Constant8ByteNumericInfo(tag: Int) : ConstantPoolEntry(tag, 2)

class ConstantLongInfo(val value: Long) : Constant8ByteNumericInfo(5) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeLong(value)
    }
}

class ConstantDoubleInfo(val value: Double) : Constant8ByteNumericInfo(6) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeDouble(value)
    }
}

class ConstantNameAndInfoType(val name: ConstantUtf8Info, val descriptor: ConstantUtf8Info) :
        ConstantPoolEntry(12) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeShort(name.index)
        out.writeShort(descriptor.index)
    }
}

class ConstantUtf8Info(val string: String) : ConstantPoolEntry(1) {
    override fun writeBytes(out: DataOutputStream) {
        val bytes = string.toByteArray("UTF-8")
        out.writeShort(bytes.size)
        bytes.forEach { out.writeByte(it.toInt()) }
    }
}

class ConstantMethodHandleInfo(val referenceKind: Byte, val reference: ConstantPoolEntry) : ConstantPoolEntry(15) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeByte(referenceKind.toInt())
        out.writeShort(reference.index)
    }
}

class ConstantMethodTypeInfo(val descriptor: ConstantUtf8Info) : ConstantPoolEntry(16) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeShort(descriptor.index)
    }
}

class ConstantInvokeDynamicInfo(val bootstrapMethodAttributeIndex: Short, val nameAndType: ConstantNameAndInfoType) :
        ConstantPoolEntry(18) {
    override fun writeBytes(out: DataOutputStream) {
        out.writeShort(bootstrapMethodAttributeIndex.toInt())
        out.writeShort(nameAndType.index)
    }
}
