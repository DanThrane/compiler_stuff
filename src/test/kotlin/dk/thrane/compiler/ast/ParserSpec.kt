package dk.thrane.compiler.ast

import org.junit.Test
import kotlin.test.assertEquals
import dk.thrane.compiler.ast.Tokens.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParserSpec {
    @Test
    fun testFunctionNoParameters() {
        val parser = Parser()
        val source = """
        func foobar()
        end foobar
        """

        val result = parser.parse(source)
        assertEquals(result.size, 1, "One function should be present")
        assertEquals(result[0].head.name, "foobar", "Name of function should be foobar")
        assertEquals(result[0].head.parameters.size, 0, "There should be no parameters present")
        assertEquals(result[0].head.name, result[0].tail.name, "Head and tail identifier should match")
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
        val head = result[0].head
        val tail = result[0].tail

        // Check basic function characteristics
        assertEquals(result.size, 1, "One functino should be present")
        assertEquals(head.name, "foobar", "Name of function should be foobar")
        assertEquals(head.parameters.size, 7, "Seven parameters should be present")
        assertEquals(head.parameters.map { it.name }, listOf("a", "b", "c", "d", "e", "f", "g"),
                "Parameters should have the correct names")

        // The the parameter types
        assertEquals(head.parameters.map { it.type.type }, listOf(T_INT, T_BOOL, T_CHAR, T_ARRAY, T_ARRAY, T_RECORD,
                T_ARRAY))
        assertTrue(head.parameters[0].type is TypeNode)
        assertTrue(head.parameters[1].type is TypeNode)
        assertTrue(head.parameters[2].type is TypeNode)
        assertTrue(head.parameters[3].type is ArrayTypeNode)
        assertTrue(head.parameters[4].type is ArrayTypeNode)
        assertTrue(head.parameters[5].type is RecordTypeNode)
        assertTrue(head.parameters[6].type is ArrayTypeNode)

        // Check the inner parameter types
        requireType(head.parameters[3].type, listOf(T_ARRAY, T_INT), "array of int")
        requireType(head.parameters[4].type, listOf(T_ARRAY, T_ARRAY, T_BOOL), "array array of bool")
        requireType(head.parameters[5].type, listOf(
                T_RECORD,
                mapOf(Pair("a", listOf(T_ARRAY, T_INT)))
        ), "record of (a: array of int)")
        requireType(head.parameters[6].type, listOf(
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
                    assertEquals(current.type, T_ARRAY)
                    current = current.arrayType
                }
                is RecordTypeNode -> {
                    if (type == T_RECORD) {
                        assertFalse(checkingRecord)
                        checkingRecord = true
                    } else if (checkingRecord) {
                        if (type is Map<*, *>) {
                            assertEquals(type.size, current.fields.size, "Not enough fields in record ($name)")
                            for (field in current.fields) {
                                requireType(field.type, type.getRaw(field.name) as List<Any>, name)
                            }
                        } else {
                            assertTrue(false, "Error in type. Expected $type to be a map")
                        }
                    }
                }
                is TypeNode -> {
                    assertEquals(current.type, type, "While checking $name. Full type is $node")
                }
            }
        }
    }
}