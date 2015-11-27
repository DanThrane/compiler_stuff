package dk.thrane.compiler.ast

import dk.thrane.compiler.type.*
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TypeSpec {
    @Test
    fun testSimpleTypeCompatibility() {
        assertTrue(Type.checkCompatibility(TypeInt(), TypeInt()), "Ints are compatible")
        assertTrue(Type.checkCompatibility(TypeBool(), TypeBool()), "Booleans are compatible")
        assertTrue(Type.checkCompatibility(TypeChar(), TypeChar()), "Chars are compatible")
        assertFalse(Type.checkCompatibility(TypeInt(), TypeChar()), "Ints and Chars are not compatible")
        assertFalse(Type.checkCompatibility(TypeInt(), TypeBool()), "Ints and Bool are not compatible")
        assertFalse(Type.checkCompatibility(TypeBool(), TypeInt()), "Bool and Int are not compatible")
        assertFalse(Type.checkCompatibility(TypeBool(), TypeChar()), "Bool and Chars are not compatible")
        assertFalse(Type.checkCompatibility(TypeChar(), TypeInt()), "Char and Int are not compatible")
        assertFalse(Type.checkCompatibility(TypeChar(), TypeBool()), "Char and Bool are not compatible")
    }

    @Test
    fun testCompatibilityWithNull() {
        assertTrue(Type.checkCompatibility(TypeArray(TypeInt()), TypeNull()), "Arrays are compatible with null")
        assertTrue(Type.checkCompatibility(TypeRecord(emptyList()), TypeNull()), "Records are compatible with null")
        assertFalse(Type.checkCompatibility(TypeNull(), TypeArray(TypeInt())), "Null isn't compatible with arrays")
        assertFalse(Type.checkCompatibility(TypeNull(), TypeRecord(emptyList())), "Null isn't compatible with records")
        assertFalse(Type.checkCompatibility(TypeNull(), TypeInt()), "Null isn't compatible with Int")
        assertFalse(Type.checkCompatibility(TypeNull(), TypeBool()), "Null isn't compatible with Bool")
        assertFalse(Type.checkCompatibility(TypeNull(), TypeChar()), "Null isn't compatible with Char")
        assertFalse(Type.checkCompatibility(TypeInt(), TypeNull()), "Int isn't compatible with null")
        assertFalse(Type.checkCompatibility(TypeBool(), TypeNull()), "Bool isn't compatible with null")
        assertFalse(Type.checkCompatibility(TypeChar(), TypeNull()), "Char isn't compatible with null")
    }

    @Test
    fun testSimpleArrayCompatibility() {
        assertTrue(Type.checkCompatibility(TypeArray(TypeInt()), TypeArray(TypeInt())), "Array<Int> is compatible with Array<Int>")
        assertFalse(Type.checkCompatibility(TypeArray(TypeInt()), TypeArray(TypeBool())), "Array<Int> is compatible with Array<Bool>")
        assertFalse(Type.checkCompatibility(TypeArray(TypeInt()), TypeArray(TypeChar())), "Array<Int> is compatible with Array<Char>")
        assertTrue(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeChar())), "Array<Char> is compatible with Array<Char>")
        assertFalse(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeInt())), "Array<Char> is compatible with Array<Int>")
        assertFalse(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeBool())), "Array<Char> is compatible with Array<Bool>")
        assertTrue(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeChar())), "Array<Char> is compatible with Array<Char>")
        assertTrue(Type.checkCompatibility(TypeArray(TypeBool()), TypeArray(TypeBool())), "Array<Bool> is compatible with Array<Bool>")
        assertFalse(Type.checkCompatibility(TypeArray(TypeBool()), TypeArray(TypeInt())), "Array<Bool> is compatible with Array<Int>")
        assertFalse(Type.checkCompatibility(TypeArray(TypeBool()), TypeArray(TypeChar())), "Array<Bool> is compatible with Array<Char>")
    }

    @Test
    fun testSimpleRecordCompatibility() {
        assertTrue(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar())))
        ), "{ a: int, b: bool, c: char } is compatible with { a: int, b: bool, c: char }")

        assertFalse(Type.checkCompatibility(
                TypeRecord(listOf(Pair("i", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar())))
        ), "{ i: int, b: bool, c: char } is not compatible with { a: int, b: bool, c: char }")

        assertFalse(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()),
                                  Pair("d", TypeInt())))
        ), "{ a: int, b: bool, c: char } is not compatible with { a: int, b: bool, c: char, d: int }")
    }

    @Test
    fun testNestedTypes() {
        assertTrue(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c",
                        TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeArray(TypeInt()))))
                ))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c",
                        TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeArray(TypeInt()))))
                )))
        ), "{ a: int, b: bool, c: {a: int, b: bool, c: Array<Int> } is compatible with " +
                "{ a: int, b: bool, c: {a: int, b: bool, c: Array<Int> }")
        assertFalse(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c",
                        TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeArray(TypeInt()))))
                ))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c",
                        TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("e", TypeArray(TypeInt()))))
                )))
        ), "{ a: int, b: bool, c: {a: int, b: bool, c: Array<Int> } is compatible with " +
                "{ a: int, b: bool, c: {a: int, b: bool, e: Array<Int> }")
    }
}

