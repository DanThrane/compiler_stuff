package dk.thrane.compiler.bytecode

import java.io.DataOutputStream

abstract class Attribute(val name: ConstantUtf8Info) {
    fun write(out: DataOutputStream) {
        out.writeShort(name.index)
        writeBody(out)
    }

    abstract fun writeBody(out: DataOutputStream)
}
