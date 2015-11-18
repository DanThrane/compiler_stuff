package dk.thrane.compiler.ast

import java.nio.file.Files
import java.nio.file.Paths

class TestVisitor : Visitor() {
    override fun enterNode(node: Node) {
        println("Entering ${node.javaClass.name}")
    }

    override fun exitNode(node: Node) {
        println("Exiting ${node.javaClass.name}")
    }
}

class FunctionFix : Visitor() {
    override fun enterNode(node: Node) {
        when (node) {
            is FunctionNode -> {
                node.tail.name = node.head.name
            }
        }
    }

    override fun exitNode(node: Node) {
        // Let children be a getter, make fully mutable
    }

}

class FunctionCheck : Visitor() {
    override fun enterNode(node: Node) {
        when (node) {
            is FunctionNode -> {
                if (node.head.name != node.tail.name) {
                    throw IllegalStateException("Name in head of function does not match name in tail of function!" +
                            " Head defined at line ${node.head.lineNumber}, tail at line ${node.tail.lineNumber}")
                }
                visitChildren = false
            }
        }
    }

    override fun exitNode(node: Node) {}
}

fun main(args: Array<String>) {
    println("Hello!")

    val source = Files.readAllLines(Paths.get("./programs", "hello.die")).joinToString("\n")
    val parser = Parser()
    val functionChecker = FunctionCheck()
    val printer = TestVisitor()

    val ast = parser.parse(source)
    printer.traverse(ast)
    functionChecker.traverse(ast)
}

