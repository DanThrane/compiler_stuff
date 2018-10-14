package dk.thrane.compiler.codegen

import dk.thrane.compiler.ast.AbsoluteNode
import dk.thrane.compiler.ast.AllocNode
import dk.thrane.compiler.ast.ArrayAccessNode
import dk.thrane.compiler.ast.AssignmentNode
import dk.thrane.compiler.ast.BinaryExpressionNode
import dk.thrane.compiler.ast.BlockNode
import dk.thrane.compiler.ast.BooleanLiteralNode
import dk.thrane.compiler.ast.CharacterLiteralNode
import dk.thrane.compiler.ast.ExpressionNode
import dk.thrane.compiler.ast.FieldAccessNode
import dk.thrane.compiler.ast.FunctionCallNode
import dk.thrane.compiler.ast.FunctionNode
import dk.thrane.compiler.ast.IfNode
import dk.thrane.compiler.ast.NegationNode
import dk.thrane.compiler.ast.Node
import dk.thrane.compiler.ast.NullLiteralNode
import dk.thrane.compiler.ast.NumberLiteralNode
import dk.thrane.compiler.ast.Operator
import dk.thrane.compiler.ast.ReturnNode
import dk.thrane.compiler.ast.StatementNode
import dk.thrane.compiler.ast.StringLiteralNode
import dk.thrane.compiler.ast.TypeDeclarationNode
import dk.thrane.compiler.ast.VariableAccessNode
import dk.thrane.compiler.ast.VariableDeclarationNode
import dk.thrane.compiler.ast.VariableNode
import dk.thrane.compiler.ast.VariableTermNode
import dk.thrane.compiler.ast.Visitor
import dk.thrane.compiler.ast.WhileNode
import dk.thrane.compiler.ast.WriteNode
import dk.thrane.compiler.type.FunctionScope
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
            is FunctionNode -> {
                // We only ever want to visit top-level nodes
                visitChildren = false

                handleFunction(node)
            }
        }
    }

    override fun exitNode(node: Node) {
        // Do nothing
    }

    private fun handleFunction(node: FunctionNode) {
        // TODO Nested functions need to receive the captured environment. They don't currently receive it.

        val nestedFunctions = node.body.declarations.filterIsInstance<FunctionNode>()
        nestedFunctions.forEach { handleFunction(it) }

        val scope = node.body.scope as FunctionScope
        val functionType = node.function
        val name = node.head.name
        val type = functionType.returnType

        val nonFunctionDeclarations = node.body.declarations.filter { it !is FunctionNode }

        output.print("${type.toCType()} $name (")
        val parameterList = functionType.parameterTypes.joinToString(", ") { (name, type) ->
            "${type.toCType()} $name"
        }
        output.print(parameterList)
        output.println(") {")

        // TODO Refactor this a bit
        nonFunctionDeclarations.forEach { declNode ->
            @Suppress("unused_variable")
            val ignored = when (declNode) {
                is VariableDeclarationNode -> {
                    declNode.variables.forEach { variable ->
                        val symbol = scope[variable.name]
                        output.println("${symbol.type.toCType()} ${symbol.name};")
                    }
                }

                is TypeDeclarationNode -> {
                    // Needs no code generation
                }

                is FunctionNode -> throw IllegalStateException("Assertion error")
            }
        }

        node.body.statements.forEach { handleStatement(it) }
        output.println("}")
    }

    private fun handleStatement(node: StatementNode) {
        @Suppress("unused_variable")
        val ignored: Unit = when (node) {
            is WriteNode -> {
                // Write some C-code
                val decl = generateExpressionDeclaration(node.expression)
                val printfThing = when (node.expression.type) {
                    is TypeInt -> "%d"
                    else -> TODO()
                }
                output.println("""printf("$printfThing\n", $decl);""")
            }

            is ReturnNode -> {
                val expression = generateExpressionDeclaration(node.expression)
                output.println("return $expression;")
            }

            is AllocNode -> TODO()

            is AssignmentNode -> {
                val expression = generateExpressionDeclaration(node.expression)

                handleVariableNode(node.variable)
                output.println(" = $expression;")
            }

            is IfNode -> {
                val ifExpression = generateExpressionDeclaration(node.expression)
                output.println("if ($ifExpression) {")
                handleStatement(node.statementNode)
                output.println("}")

                val elseNode = node.elseNode
                if (elseNode != null) {
                    output.println("else {")
                    handleStatement(elseNode)
                    output.println("}")
                }

                Unit
            }

            is WhileNode -> {
                output.println("while (1) {")
                val whileExpression = generateExpressionDeclaration(node.expression)
                output.println("if (!$whileExpression) break;")
                handleStatement(node.statementNode)
                output.println("}")
            }

            is BlockNode -> node.statements.forEach { handleStatement(it) }
        }
    }

    private fun generateExpressionDeclaration(node: ExpressionNode): String {
        val exprName = "expr${node.serialNumber}"
        val varDecl = "${node.type.toCType()} $exprName = "

        @Suppress("unused_variable")
        val ignored = when (node) {
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

            is VariableTermNode -> {
                output.print(varDecl)
                handleVariableNode(node.variableNode)
                output.println(";")
            }

            is FunctionCallNode -> {
                val expressions = node.arguments.map { generateExpressionDeclaration(it) }
                output.println("$varDecl ${node.name}(${expressions.joinToString(", ")});")
            }

            is NegationNode -> {
                val expr = generateExpressionDeclaration(node.expressionNode)
                output.println("$varDecl !$expr;")
            }

            is AbsoluteNode -> {
                val expr = generateExpressionDeclaration(node.expressionNode)
                output.println("$varDecl abs($expr);")
            }

            is BooleanLiteralNode -> {
                output.print(varDecl)
                val toInt = if (node.value) "1" else "0"
                output.print(toInt)
                output.println(";")
            }

            is NullLiteralNode -> {
                output.print(varDecl)
                output.println("0;")
            }

            is StringLiteralNode -> {
                output.print(varDecl)
                output.print('"')
                output.print(node.value)
                output.print('"')
                output.println(';')
            }

            is CharacterLiteralNode -> {
                output.print(varDecl)
                output.print("'${node.value}'")
                output.println(";")
            }
        }

        return exprName
    }

    private fun handleVariableNode(node: VariableNode) {
        @Suppress("unused_variable")
        val ignored = when (node) {
            is VariableAccessNode -> output.print(node.identifier)

            is ArrayAccessNode -> {
                val expr = generateExpressionDeclaration(node.expressionNode)

                handleVariableNode(node.variableAccessNode)
                output.print("[$expr]")
            }

            is FieldAccessNode -> {
                handleVariableNode(node.variableAccessNode)
                output.print(".${node.fieldName}")
            }
        }
    }

    private fun Type.toCType(): String = when (this) {
        is TypeInt -> "int"
        is TypeBool -> "int"
        is TypeChar -> "char"
        is TypeUnit -> "void"
        else -> "void"
    }
}
