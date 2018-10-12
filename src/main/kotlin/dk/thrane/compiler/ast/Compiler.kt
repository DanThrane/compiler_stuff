package dk.thrane.compiler.ast

import dk.thrane.compiler.type.SymbolGatherer
import dk.thrane.compiler.type.TypeChecker
import dk.thrane.compiler.weeder.ReturnCheck
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    println("Hello!")

    val source = Files.readAllLines(Paths.get("./programs", "nested.die")).joinToString("\n")
    val parser = Parser()
    val functionChecker = FunctionCheck()
    val gatherer = SymbolGatherer()
    val checker = TypeChecker()
    val returnCheck = ReturnCheck()

    val ast = parser.parse(source)
    gatherer.traverse(ast)
    // FIXME NEEDS TO BE SUPPORTED a[0][1][2];
    functionChecker.traverse(ast)
    returnCheck.traverse(ast)
    checker.traverse(ast)
}

