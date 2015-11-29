package dk.thrane.compiler.bytecode

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

/**
 * Represents the code attribute. This attribute contains JVM instructions and auxiliary information for a method. See
 * JVMS8 §4.7.3.
 */
class CodeAttribute(name: ConstantUtf8Info, val maxStack: Int, val maxLocals: Int, val code: ByteArray,
                    val exceptionTables: List<ExceptionTable>, val attributes: List<Attribute>) : Attribute(name) {
    override fun writeBody(out: DataOutputStream) {
        // Write into a temporary buffer to calculate the length of this attribute
        val byteBuffer = ByteArrayOutputStream()
        val buffer = DataOutputStream(byteBuffer)

        buffer.writeShort(maxStack)
        buffer.writeShort(maxLocals)
        buffer.writeInt(code.size)
        buffer.write(code)
        buffer.writeShort(exceptionTables.size)
        exceptionTables.forEach { it.write(buffer) }
        buffer.writeShort(attributes.size)
        attributes.forEach { it.write(buffer) }

        // Write the temporary buffer to the output buffer
        out.writeInt(buffer.size())
        out.write(byteBuffer.toByteArray())
    }
}
