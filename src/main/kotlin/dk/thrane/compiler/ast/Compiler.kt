package dk.thrane.compiler.ast

import java.nio.file.Files
import java.nio.file.Paths

import dk.thrane.compiler.ast.Tokens.*

class TestVisitor : Visitor() {
    var indent: Int = 0
    override fun enterNode(node: Node) {
        var indention = CharArray(indent)
        indention.fill(' ')
        println("${String(indention)}Entering ${node.javaClass.name}")
        indent += 2
    }

    override fun exitNode(node: Node) {
        indent -= 2
        var indention = CharArray(indent)
        indention.fill(' ')
        println("${String(indention)}Exiting ${node.javaClass.name}")
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

    override fun exitNode(node: Node) {
    }
}

fun main(args: Array<String>) {
    println("Hello!")

    val source = Files.readAllLines(Paths.get("./programs", "operators.die")).joinToString("\n")
    val parser = Parser()
    val functionFix = FunctionFix()
    val functionChecker = FunctionCheck()
    val printer = TestVisitor()

    val ast = parser.parse(source)
    printer.traverse(ast)
    functionFix.traverse(ast)
    functionChecker.traverse(ast)
}

