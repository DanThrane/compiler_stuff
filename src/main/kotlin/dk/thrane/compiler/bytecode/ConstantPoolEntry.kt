package dk.thrane.compiler.bytecode

import java.nio.charset.Charset

/**
 * Represents a single entry in the constant pool
 */
open class ConstantPoolEntry(val tag: Byte, val info: Array<Byte>)

/**
 * Represents a class or an interface. The name index must be a valid index into the constant pool table, pointing to a
 * [ConstantUtf8Info] structure, which should represent a valid binary class or interface encoded in the internal form
 * (§4.2.1)
 */
class ConstantClassInfo(val nameIndex: Short) : ConstantPoolEntry(7, arrayOf(0, 0))

/**
 * Represents a reference to a field, method, or a reference.
 *
 * The value of the [classIndex] item must be a valid index into the constant_pool table. The  constant_pool entry at
 * that index must be a [ConstantClassInfo] structure (§4.4.1) representing a class or interface type that has the
 * field or method as a member.
 *
 * The value of the [nameAndTypeIndex] item must be a valid index into the constant pool table. The [ConstantPoolEntry]
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
abstract class ConstantRefInfo(tag: Byte, val classIndex: Short, val nameAndTypeIndex: Short) :
        ConstantPoolEntry(tag, arrayOf(0, 0))

class ConstantFieldRefInfo(classIndex: Short, nameAndTypeIndex: Short) :
        ConstantRefInfo(9, classIndex, nameAndTypeIndex)

class ConstantMethodRefInfo(classIndex: Short, nameAndTypeIndex: Short) :
        ConstantRefInfo(10, classIndex, nameAndTypeIndex)

class ConstantInterfaceMethodRefInfo(classIndex: Short, nameAndTypeIndex: Short) :
        ConstantRefInfo(11, classIndex, nameAndTypeIndex)

/**
 * Used to represent constant objects of the type String
 *
 * The value of the [stringIndex] item must be a valid index into the constant pool table. The [ConstantPoolEntry] at
 * that index must be a [ConstantUtf8Info] structure (§4.4.7) representing the sequence of Unicode code points to
 * which the String object is to be initialized.
 */
class ConstantStringInfo(val stringIndex: Short) : ConstantPoolEntry(8, arrayOf(0, 0))

abstract class Constant4ByteNumericInfo(tag: Byte, bytes: Array<Byte>) : ConstantPoolEntry(tag, bytes)

/**
 * The bytes item of the [ConstantIntegerInfo] structure represents the value of the  int constant. The bytes of the
 * value are stored in big-endian (high byte first) order.
 */
class ConstantIntegerInfo(val value: Int) : Constant4ByteNumericInfo(3, arrayOf())

/**
 * The bytes item of the [ConstantFloatInfo] structure represents the value of the float constant in IEEE 754
 * floating-point single format (§2.3.2). The bytes of the single format representation are stored in big-endian
 * (high byte first) order.
 */
class ConstantFloatInfo(val value: Float) : Constant4ByteNumericInfo(4, arrayOf())

/**
 * #IMPORTANT:
 * All 8-byte constants take up two entries in the constant_pool table of the class file. If a
 * [Constant8ByteNumericInfo] is the item in the constant pool table at index n, then the next usable item in
 * the pool is located at index n + 2. The  constant pool index n + 1 must be valid but is considered unusable.
 */
abstract class Constant8ByteNumericInfo(tag: Byte, bytes: Array<Byte>) : ConstantPoolEntry(tag, bytes)

class ConstantLongInfo(val value: Long) : Constant8ByteNumericInfo(5, arrayOf())
class ConstantDoubleInfo(val value: Double) : Constant8ByteNumericInfo(6, arrayOf())

class ConstantNameAndInfoType(val nameIndex: Short, val descriptorIndex: Short) : ConstantPoolEntry(12, arrayOf())
class ConstantUtf8Info(val string: String) : ConstantPoolEntry(1, arrayOf()/*Charset.forName("UTF-8").encode(string)*/)
class ConstantMethodHandleInfo(val referenceKind: Byte, val referenceIndex: Short) : ConstantPoolEntry(15, arrayOf())
class ConstantMethodTypeInfo(val descriptorIndex: Short) : ConstantPoolEntry(16, arrayOf())
class constantInvokeDynamicInfo(val bootstrapMethodAttributeIndex: Short, val nameAndTypeIndex: Short) :
        ConstantPoolEntry(18, arrayOf())
