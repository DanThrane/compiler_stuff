package dk.thrane.compiler.ast

enum class Operator(val token: Tokens, val rightAssociativite: Boolean, val precedense: Int) {
    MULTIPLICATION(Tokens.T_MULTIPLICATION, false, 7),
    DIVISION(Tokens.T_DIVISION, false, 7),
    MODULO(Tokens.T_PERCENT_SIGN, false, 7),
    ADD(Tokens.T_ADD, false, 6),
    MINUS(Tokens.T_SUBTRACTION, false, 6),
    COMPARE_LESS_THAN(Tokens.T_COMPARE_LESS_THAN, false, 5),
    COMPARE_GREATER_THAN(Tokens.T_COMPARE_GREATER_THAN, false, 5),
    COMPARE_LESS_THAN_OR_EQUALS(Tokens.T_COMPARE_LESS_THAN_OR_EQUALS, false, 5),
    COMPARE_GREATER_THAN_OR_EQUALS(Tokens.T_COMPARE_GREATER_THAN_OR_EQUALS, false, 5),
    COMPARE_EQUAL(Tokens.T_COMPARE_EQUALS, false, 4),
    COMPARE_NOT_EQUAL(Tokens.T_COMPARE_NOT_EQUALS, false, 4),
    AND(Tokens.T_AND, false, 3),
    OR(Tokens.T_OR, false, 2);
}

