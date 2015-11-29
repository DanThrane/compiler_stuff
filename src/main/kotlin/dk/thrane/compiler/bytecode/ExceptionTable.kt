package dk.thrane.compiler.bytecode

import java.io.DataOutputStream

class ExceptionTable(val startProgramCounter: Int, val endProgramCounter: Int, val handler: Int, val catchType: Int) {
    fun write(out: DataOutputStream) {
        out.writeShort(startProgramCounter)
        out.writeShort(endProgramCounter)
        out.writeShort(handler)
        out.writeShort(catchType)
    }
}