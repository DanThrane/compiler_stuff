package dk.thrane.compiler.codegen

import dk.thrane.compiler.ast.BinaryExpressionNode
import dk.thrane.compiler.ast.ExpressionNode
import dk.thrane.compiler.ast.FunctionNode
import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.NumberLiteralNode
import dk.thrane.compiler.ast.Operator
import dk.thrane.compiler.ast.StatementNode
import dk.thrane.compiler.ast.Visitor
import dk.thrane.compiler.ast.WriteNode
import dk.thrane.compiler.type.Type
import dk.thrane.compiler.type.TypeBool
import dk.thrane.compiler.type.TypeChar
import dk.thrane.compiler.type.TypeInt
import dk.thrane.compiler.type.TypeUnit
import java.io.PrintStream

class Generator : Visitor() {
    private val output = PrintStream(System.out)

    override fun enterNode(node: Node) {
        when (node) {
            is StatementNode -> {
                handleStatements(node)
            }

            is FunctionNode -> {
                writeFunctionHead(node)
            }
        }
    }

    override fun exitNode(node: Node) {
        when (node) {
            is FunctionNode -> {
                writeFunctionTail(node)
            }
        }
    }

    private fun writeFunctionHead(node: FunctionNode) {
        val functionType = node.function
        val name = node.head.name
        val type = functionType.returnType

        output.print("${type.toCType()} $name (")
        val parameterList = functionType.parameterTypes.joinToString(", ") { (name, type) ->
            "${type.toCType()} $name"
        }
        output.print(parameterList)
        output.println(") {")
    }

    private fun writeFunctionTail(node: FunctionNode) {
        output.println("}")
    }

    private fun handleStatements(node: StatementNode) {
        visitChildren = false

        when (node) {
            is WriteNode -> {
                // Write some C-code
                val decl = generateExpressionDeclaration(node.expression)
                val printfThing = when (node.expression.type) {
                    is TypeInt -> "%d"
                    else -> TODO()
                }
                output.println("""printf("$printfThing\n", $decl);""")
            }
        }
    }

    private fun generateExpressionDeclaration(node: ExpressionNode): String {
        val exprName = "expr${node.serialNumber}"
        val varDecl = "${node.type.toCType()} $exprName = "

        when (node) {
            is BinaryExpressionNode -> {
                val left = generateExpressionDeclaration(node.left)
                val right = generateExpressionDeclaration(node.right)
                output.print(varDecl)
                output.print(left)
                val operator = when (node.operator) {
                    Operator.MULTIPLICATION -> "*"
                    Operator.DIVISION -> "/"
                    Operator.MODULO -> "%"
                    Operator.ADD -> "+"
                    Operator.MINUS -> "0"
                    Operator.COMPARE_LESS_THAN -> "<"
                    Operator.COMPARE_GREATER_THAN -> ">"
                    Operator.COMPARE_LESS_THAN_OR_EQUALS -> "<="
                    Operator.COMPARE_GREATER_THAN_OR_EQUALS -> ">="
                    Operator.COMPARE_EQUAL -> "=="
                    Operator.COMPARE_NOT_EQUAL -> "!="
                    Operator.AND -> "&&"
                    Operator.OR -> "||"
                }
                output.print(operator)
                output.print(right)
                output.println(";")
            }

            is NumberLiteralNode -> {
                output.print(varDecl)
                output.print(node.value)
                output.println(";")
            }
        }

        return exprName
    }

    private fun Type.toCType(): String = when (this) {
        is TypeInt -> "int"
        is TypeBool -> "int"
        is TypeChar -> "char"
        is TypeUnit -> "void"
        else -> "void"
    }
}
