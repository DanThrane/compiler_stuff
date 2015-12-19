package dk.thrane.compiler.bytecode

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.util.*

class CodeBuilder(val constantPool: ConstantPool) {
    companion object {
        const val CODE_NAME = "Code"

        fun build(constantPool: ConstantPool, init: CodeBuilder.() -> Unit): CodeAttribute {
            val code = CodeBuilder(constantPool)
            code.init()
            return code.build()
        }
    }

    var maxStack: Int = 0
    var maxLocals: Int = 0
    private var code: List<Instruction> = emptyList()
    private val exceptionTable: MutableList<ExceptionTable> = ArrayList()
    private val attributes: MutableList<Attribute> = ArrayList()

    fun code(init: InstructionBuilder.() -> Unit) {
        val instructionBuilder = InstructionBuilder(constantPool)
        instructionBuilder.init()
        code = instructionBuilder.buildCode()
    }

    fun build(): CodeAttribute {
        val name = constantPool.utf8(CODE_NAME)
        val byteStream = ByteArrayOutputStream()
        val outputStream = DataOutputStream(byteStream)
        code.forEach { it.write(outputStream) }

        return CodeAttribute(name, maxStack, maxLocals, byteStream.toByteArray(), exceptionTable, attributes)
    }

}

class InstructionBuilder(val pool: ConstantPool) {
    private val code: MutableList<Instruction> = ArrayList()

    operator fun Instruction.unaryPlus() {
        code += this
    }

    fun ldc(entries: ConstantPool.() -> ConstantPoolEntry) {
        val entry = entries(pool)
        code += Instruction.ldc(entry)
    }

    fun getstatic(entries: ConstantPool.() -> ConstantFieldRefInfo) {
        val entry = entries(pool)
        code += Instruction.getstatic(entry)
    }

    fun invokevirtual(entries: ConstantPool.() -> ConstantMethodRefInfo) {
        val entry = entries(pool)
        code += Instruction.invokevirtual(entry)
    }

    fun buildCode(): List<Instruction> = code

    val `return` = Instruction.`return`
}
