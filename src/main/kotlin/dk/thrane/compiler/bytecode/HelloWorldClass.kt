/**
 * Attempts to create a hello world class
 */
package dk.thrane.compiler.bytecode

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

fun main(args: Array<String>) {
    val file = ClassFile()

    val name = ConstantUtf8Info("Test")
    val classInfo = ConstantClassInfo(name)
    file.constantPool.insertEntry(name)
    file.constantPool.insertEntry(classInfo)

    file.thisClass = classInfo
    file.accessFlags.add(AccessFlag.ACC_PUBLIC)

    val mainArguments = ConstantUtf8Info(ArrayType(ObjectType("java/lang/String")).toString())
    val mainType = ConstantUtf8Info(MethodDescriptor(listOf(), VoidDescriptor()).toString())
    val mainName = file.constantPool.insertString("main")
    val codeName = file.constantPool.insertString("Code")
    val mainCodeByteStream = ByteArrayOutputStream()
    val mainCodeOutput = DataOutputStream(mainCodeByteStream)

    val helloString = file.constantPool.insertString("Hello world!")
    val systemClass = ConstantClassInfo(file.constantPool.insertString("java/lang/System"))
    val outNameAndInfo = ConstantNameAndInfoType(file.constantPool.insertString("out"),
            file.constantPool.insertString(ObjectType("java/io/PrintStream").toString()))
    file.constantPool.insertEntry(systemClass)
    file.constantPool.insertEntry(outNameAndInfo)
    file.constantPool.insertEntry(ConstantFieldRefInfo(systemClass, outNameAndInfo))
    Instruction.getstatic()

    val code = CodeAttribute(codeName, 2, 1, )
    val main = Method(listOf(MethodAccessFlag.ACC_PUBLIC, MethodAccessFlag.ACC_STATIC), mainName, mainType,
            listOf(code))
}
