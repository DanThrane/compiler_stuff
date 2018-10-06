package dk.thrane.compiler.ast

import org.junit.Test
import dk.thrane.compiler.ast.Tokens.*
import junit.framework.TestCase.*

class ParserSpec {
    @Test
    fun testFunctionNoParameters() {
        val parser = Parser()
        val source = """
        func foobar()
        end foobar
        """

        val result = parser.parse(source)
        assertEquals(result.functions.size, 1)
        assertEquals(result.functions[0].head.name, "foobar")
        assertEquals(result.functions[0].head.parameters.size, 0)
        assertEquals(result.functions[0].head.name, result.functions[0].tail.name)
    }

    @Test
    fun testFunctionWithParameters() {
        val parser = Parser()
        val source = """
        func foobar(a: int, b: bool, c: char, d: array of int, e: array of array of bool,
                    f: record of {a: array of int}, g: array of record of {a: int})
        end foobar
        """

        val result = parser.parse(source)
        val head = result.functions[0].head

        // Check basic function characteristics
        assertEquals(result.functions.size, 1)
        assertEquals(head.name, "foobar")
        assertEquals(head.parameters.size, 7)
        assertEquals(head.parameters.map { it.name }, listOf("a", "b", "c", "d", "e", "f", "g"))

        // The the parameter types
//        assertEquals(head.parameters.map { it.typeNode }, listOf(T_INT, T_BOOL, T_CHAR, T_ARRAY, T_ARRAY, T_RECORD,
//                T_ARRAY))
        assertTrue(head.parameters[0].typeNode is TypeNode)
        assertTrue(head.parameters[1].typeNode is TypeNode)
        assertTrue(head.parameters[2].typeNode is TypeNode)
        assertTrue(head.parameters[3].typeNode is ArrayTypeNode)
        assertTrue(head.parameters[4].typeNode is ArrayTypeNode)
        assertTrue(head.parameters[5].typeNode is RecordTypeNode)
        assertTrue(head.parameters[6].typeNode is ArrayTypeNode)

        // Check the inner parameter types
        requireType(head.parameters[3].typeNode, listOf(T_ARRAY, T_INT), "array of int")
        requireType(head.parameters[4].typeNode, listOf(T_ARRAY, T_ARRAY, T_BOOL), "array array of bool")
        requireType(head.parameters[5].typeNode, listOf(
                T_RECORD,
                mapOf(Pair("a", listOf(T_ARRAY, T_INT)))
        ), "record of (a: array of int)")
        requireType(head.parameters[6].typeNode, listOf(
                T_ARRAY,
                T_RECORD,
                mapOf(Pair("a", listOf(T_INT)))
        ), "array of record of (a: int)")
    }

    private fun requireType(node: TypeNode, types: List<Any>, name: String) {
        var current = node
        var checkingRecord = false
        for (type in types) {
            when (current) {
                is ArrayTypeNode -> {
//                    assertEquals(current.type, T_ARRAY)
                    current = current.arrayType
                }
                is RecordTypeNode -> {
                    if (type == T_RECORD) {
                        assertFalse(checkingRecord)
                        checkingRecord = true
                    } else if (checkingRecord) {
                        if (type is Map<*, *>) {
                            val map = type as Map<String, Any>

                            assertEquals(type.size, current.fields.size)
                            for (field in current.fields) {
                                @Suppress("UNCHECKED_CAST")
                                requireType(field.typeNode, map[field.name] as List<Any>, name)
                            }
                        } else {
                            assertTrue(false)
                        }
                    }
                }

                else -> {
//                    assertEquals(current.type, type, "While checking $name. Full type is $node")
                }
            }
        }
    }
}