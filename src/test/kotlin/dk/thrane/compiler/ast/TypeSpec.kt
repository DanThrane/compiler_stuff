package dk.thrane.compiler.ast

import dk.thrane.compiler.type.*
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TypeSpec {
    @Test
    fun testSimpleTypeCompatibility() {
        assertTrue(Type.checkCompatibility(TypeInt(), TypeInt()))
        assertTrue(Type.checkCompatibility(TypeBool(), TypeBool()))
        assertTrue(Type.checkCompatibility(TypeChar(), TypeChar()))
        assertFalse(Type.checkCompatibility(TypeInt(), TypeChar()))
        assertFalse(Type.checkCompatibility(TypeInt(), TypeBool()))
        assertFalse(Type.checkCompatibility(TypeBool(), TypeInt()))
        assertFalse(Type.checkCompatibility(TypeBool(), TypeChar()))
        assertFalse(Type.checkCompatibility(TypeChar(), TypeInt()))
        assertFalse(Type.checkCompatibility(TypeChar(), TypeBool()))
    }

    @Test
    fun testCompatibilityWithNull() {
        assertTrue(Type.checkCompatibility(TypeArray(TypeInt()), TypeNull()))
        assertTrue(Type.checkCompatibility(TypeRecord(emptyList()), TypeNull()))
        assertFalse(Type.checkCompatibility(TypeNull(), TypeArray(TypeInt())))
        assertFalse(Type.checkCompatibility(TypeNull(), TypeRecord(emptyList())))
        assertFalse(Type.checkCompatibility(TypeNull(), TypeInt()))
        assertFalse(Type.checkCompatibility(TypeNull(), TypeBool()))
        assertFalse(Type.checkCompatibility(TypeNull(), TypeChar()))
        assertFalse(Type.checkCompatibility(TypeInt(), TypeNull()))
        assertFalse(Type.checkCompatibility(TypeBool(), TypeNull()))
        assertFalse(Type.checkCompatibility(TypeChar(), TypeNull()))
    }

    @Test
    fun testSimpleArrayCompatibility() {
        assertTrue(Type.checkCompatibility(TypeArray(TypeInt()), TypeArray(TypeInt())))
        assertFalse(Type.checkCompatibility(TypeArray(TypeInt()), TypeArray(TypeBool())))
        assertFalse(Type.checkCompatibility(TypeArray(TypeInt()), TypeArray(TypeChar())))
        assertTrue(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeChar())))
        assertFalse(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeInt())))
        assertFalse(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeBool())))
        assertTrue(Type.checkCompatibility(TypeArray(TypeChar()), TypeArray(TypeChar())))
        assertTrue(Type.checkCompatibility(TypeArray(TypeBool()), TypeArray(TypeBool())))
        assertFalse(Type.checkCompatibility(TypeArray(TypeBool()), TypeArray(TypeInt())))
        assertFalse(Type.checkCompatibility(TypeArray(TypeBool()), TypeArray(TypeChar())))
    }

    @Test
    fun testSimpleRecordCompatibility() {
        assertTrue(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar())))
        ))

        assertFalse(Type.checkCompatibility(
                TypeRecord(listOf(Pair("i", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar())))
        ))

        assertFalse(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeChar()),
                                  Pair("d", TypeInt())))
        ))
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
        ))

        assertFalse(Type.checkCompatibility(
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c",
                        TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c", TypeArray(TypeInt()))))
                ))),
                TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("c",
                        TypeRecord(listOf(Pair("a", TypeInt()), Pair("b", TypeBool()), Pair("e", TypeArray(TypeInt()))))
                )))
        ))
    }
}

