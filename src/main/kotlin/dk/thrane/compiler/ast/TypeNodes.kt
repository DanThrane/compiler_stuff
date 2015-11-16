package dk.thrane.compiler.ast

open class TypeNode(val lineNumber: Int, val type: Tokens)
class RecordTypeNode(lineNumber: Int, val fields: List<VariableDeclarationNode>) : TypeNode(lineNumber, Tokens.T_RECORD)
class ArrayTypeNode(lineNumber: Int, val arrayType: TypeNode) : TypeNode(lineNumber, Tokens.T_ARRAY)