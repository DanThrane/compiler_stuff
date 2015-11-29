package dk.thrane.compiler.bytecode

import java.io.DataOutputStream

abstract class Attribute(val name: ConstantUtf8Info) {
    fun write(out: DataOutputStream) {
        out.writeShort(name.index)
    }

    abstract fun writeBody(out: DataOutputStream)
}
