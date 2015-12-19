/**
 * Attempts to create a hello world class
 */
package dk.thrane.compiler.bytecode

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileOutputStream

fun main(args: Array<String>) {
    val file = ClassFile()

    val classInfo = file.constantPool.classRef("Hello")
    file.thisClass = classInfo
    file.accessFlags.add(AccessFlag.ACC_PUBLIC)
    file.accessFlags.add(AccessFlag.ACC_SUPER)
    file.superClass = file.constantPool.classRef("java/lang/Object")

    val helloCode = CodeBuilder.build(file.constantPool) {
        maxStack = 2
        maxLocals = 1

        code {
            getstatic { fieldRef(classRef("java/lang/System"), nameAndType("out", ObjectDescriptor("java/io/PrintStream"))) }
            ldc { string("Hello World!") }
            invokevirtual {
                methodRef(classRef("java", "io", "PrintStream"),
                        nameAndType("println",
                                MethodDescriptor(listOf(ObjectDescriptor("java", "lang", "String")), VoidDescriptor)))
            }
            +`return`
        }
    }

    file.methods += Method(
            listOf(MethodAccessFlag.ACC_PUBLIC, MethodAccessFlag.ACC_STATIC),
            file.constantPool.utf8("main"),
            file.constantPool.descriptor(
                    MethodDescriptor(
                            listOf(ArrayDescriptor(ObjectDescriptor("java", "lang", "String"))),
                            VoidDescriptor
                    )
            ),
            listOf(helloCode)
    )
    file.write(DataOutputStream(FileOutputStream("Hello.class")))
}
