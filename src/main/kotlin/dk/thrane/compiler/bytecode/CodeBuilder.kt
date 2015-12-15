package dk.thrane.compiler.bytecode

class CodeBuilder(val constantPool: ConstantPool) {


    fun build(): CodeAttribute {
        constantPool.insertString("Code")
        throw RuntimeException("NYI")
    }
}

