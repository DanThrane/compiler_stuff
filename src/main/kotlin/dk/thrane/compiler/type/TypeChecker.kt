package dk.thrane.compiler.type

import dk.thrane.compiler.ast.*

class TypeChecker : Visitor() {
    override fun enterNode(node: Node) {}

    override fun exitNode(node: Node) {
        when (node) {
            is TermNode -> checkTermNode(node)
            is ExpressionNode -> checkExpressionNode(node)
            is VariableNode -> checkVariableNode(node)
            is StatementNode -> checkStatementNode(node)
        }
    }

    private fun checkTermNode(node: TermNode) {
        when (node) {
            is NumberLiteralNode -> node.type = TypeInt()
            is BooleanLiteralNode -> node.type = TypeBool()
            is NullLiteralNode -> node.type = TypeNull()
            is VariableTermNode -> node.type = node.variableNode.type
            is FunctionCallNode -> {
                val symbol = node.scope!!.getSymbolAndLevels(node.name) ?:
                        throw IllegalStateException("Function of name ${node.name} not found at line " +
                                "${node.lineNumber}")

                if (symbol.first.type !is TypeFunction) {
                    throw IllegalStateException("Expected ${node.name} on line ${node.lineNumber} to reference " +
                            "a function.")
                }
                // NOTE: We should really have a way of locking an object, such that its value may no longer change
                val functionType = symbol.first.type as TypeFunction
                if (node.arguments.size != functionType.parameterTypes.size) {
                    throw IllegalStateException("Expected ${functionType.parameterTypes.size} parameters for " +
                            "function ${node.name}, but got ${node.arguments.size} arguments.")
                }
                for (i in 0..node.arguments.size - 1) {
                    val given = node.arguments[i].type!!
                    val expected = functionType.parameterTypes[i].second
                    if (!Type.checkCompatibility(expected, given)) {
                        throw IllegalStateException("Type mismatch at line ${node.lineNumber}. Expected parameter " +
                                "$i to be of type $expected, but instead got $given")
                    }
                }
                node.type = functionType.returnType
            }
            is ParenthesisTermNode -> node.type = node.expressionNode.type
            is NegationNode -> {
                if (!Type.checkCompatibility(TypeBool(), node.termNode.type!!)) {
                    throw IllegalStateException("Negation can only be applied to boolean types")
                }
                node.type = TypeBool()
            }
            is AbsoluteNode -> {
                val isNumber = Type.checkCompatibility(TypeInt(), node.expressionNode.type!!)
                val isArray = node.expressionNode.type!! is TypeArray
                if (isNumber || isArray) {
                    node.type = TypeInt()
                } else {
                    throw IllegalStateException("The absolute operator can only be applied to arrays or numbers!")
                }
            }
        }
    }

    private fun checkBinaryExpression(expectedLeft: List<Type>, expectedRight: List<Type>, node: BinaryExpressionNode) {
        val leftInvalid = expectedLeft.any { !Type.checkCompatibility(it, node.left.type!!) }
        val rightInvalid = expectedLeft.any { !Type.checkCompatibility(it, node.right.type!!) }
        if (leftInvalid) {
            throw IllegalStateException("On line ${node.lineNumber}: Left side of ${node.operator} isn't " +
                    "$expectedLeft!")
        }
        if (rightInvalid) {
            throw IllegalStateException("On line ${node.lineNumber}: Right side of ${node.operator} isn't " +
                    "$expectedRight!")
        }
    }

    private fun checkExpressionNode(node: ExpressionNode) {
        when (node) {
            is BinaryExpressionNode -> {
                when (node) {
                    is MultiplicationNode, is DivisionNode, is AddNode, is MinusNode, is ModuloNode -> {
                        checkBinaryExpression(listOf(TypeInt()), listOf(TypeInt()), node)
                        node.type = TypeInt()
                    }
                    is CompareGreaterThanOrEqualsNode, is CompareGreaterThanNode, is CompareLessThanOrEqualsNode,
                    is CompareLessThanNode -> {
                        checkBinaryExpression(listOf(TypeInt()), listOf(TypeInt()), node)
                        node.type = TypeBool()
                    }
                    is AndNode, is OrNode -> {
                        checkBinaryExpression(listOf(TypeBool()), listOf(TypeBool()), node)
                        node.type = TypeBool()
                    }
                    is CompareEqualNode, is CompareNotEqualNode -> {
                        if (Type.checkCompatibility(node.left.type!!, node.right.type!!)) {
                            throw IllegalStateException("Cannot compare ${node.left.type} and ${node.right.type}")
                        }
                        node.type = TypeBool()
                    }
                }
            }
            is TermExpressionNode -> node.type = node.term.type
        }
    }

    private fun checkVariableNode(node: VariableNode) {
        when (node) {
            is VariableAccessNode -> {
                val symbol = node.scope!!.getSymbolAndLevels(node.identifier)!!.first
                node.type = symbol.type
            }
            is ArrayAccessNode -> {
                val type = Type.fullyResolve(node.variableAccessNode.type!!)
                if (type !is TypeArray) {
                    throw IllegalStateException("Attempting array access on a non-array type!")
                }
                if (!Type.checkCompatibility(TypeInt(), node.expressionNode.type!!)) {
                    throw IllegalStateException("Attempting to access array with non-integer index!")
                }
                node.type = type.type
            }
            is FieldAccessNode -> {
                val type = Type.fullyResolve(node.variableAccessNode.type!!)
                if (type !is TypeRecord) {
                    throw IllegalStateException("Attempting to access field on a non-record type!")
                }
                val field = type.fieldTypes.find { it.first == node.fieldName } ?:
                        throw IllegalStateException("Field of name ${node.fieldName} does not exist on record " +
                                "of type $type")
                node.type = field.second
            }
        }
    }

    private fun checkStatementNode(node: StatementNode) {
        when (node) {
            is ReturnNode -> {
                val returnType = node.scope!!.function!!.returnType
                if (!Type.checkCompatibility(returnType, node.expression.type!!)) {
                    throw IllegalStateException("Expected function to return $returnType, but instead got " +
                            "${node.expression.type!!}")
                }
            }
            is WriteNode -> {
                when (node.expression.type!!) {
                    is TypeInt, is TypeBool -> {}
                    else -> throw IllegalStateException("Write not supported for this type: ${node.expression.type!!}}")
                }
            }
            is AllocNode -> {
                val varType = Type.fullyResolve(node.variable.type!!)
                when (varType) {
                    is TypeArray -> {
                        if (node.expression == null) {
                            throw IllegalStateException("Length needed for array")
                        } else {
                            if (!Type.checkCompatibility(TypeInt(), node.expression!!.type!!)) {
                                throw IllegalStateException("Expected int for array length, but got " +
                                        "${node.expression!!.type!!}")
                            }
                        }
                    }
                    is TypeRecord -> {
                        if (node.expression != null) {
                            throw IllegalStateException("Didn't expect length for record allocation!")
                        }
                    }
                    else -> {
                        throw IllegalStateException("Cannot allocate object of type $varType}")
                    }
                }
            }
            is AssignmentNode -> {
                if (!Type.checkCompatibility(node.variable.type!!, node.expression.type!!)) {
                    throw IllegalStateException("Expected ${node.variable.type!!}, but got ${node.expression.type!!}")
                }
            }
            is IfNode -> {
                if (!Type.checkCompatibility(TypeBool(), node.expression.type!!)) {
                    throw IllegalStateException("Expected if statement to contain boolean expression, but instead " +
                            "got ${node.expression.type!!}")
                }
            }
            is WhileNode -> {
                if (!Type.checkCompatibility(TypeBool(), node.expression.type!!)) {
                    throw IllegalStateException("Expected while statement to contain boolean expression, but instead " +
                            "got ${node.expression.type!!}")
                }
            }
            is BlockNode -> {}
        }
    }
}