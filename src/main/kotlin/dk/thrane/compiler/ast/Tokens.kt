package dk.thrane.compiler.ast

import java.util.regex.Pattern

enum class Tokens {
    T_MULT,
    T_DIV,
    T_ADD,
    T_SUB,
    T_LPAR("\\("),
    T_RPAR("\\)"),
    T_EQUAL,
    T_CEQ,
    T_CNE,
    T_CLE,
    T_CRE,
    T_LEQ,
    T_GEQ,
    T_LBRACE,
    T_RBRACE,
    T_PER,
    T_BANG,
    T_OR,
    T_VBAR,
    T_COLON,
    T_SCOLON,
    T_DOT,
    T_COMMA,
    T_LSBRACE,
    T_RSBRACE,
    T_AND,
    T_FUNC("func"),
    T_INT,
    T_CHAR,
    T_BOOL,
    T_ARRAY,
    T_RECORD,
    T_END("end"),
    T_VAR,
    T_RETURN,
    T_WRITE,
    T_ALLOC,
    T_IF,
    T_THEN,
    T_WHILE,
    T_DO,
    T_LENGTH,
    T_ELSE,
    T_TRUE,
    T_FALSE,
    T_NULL,
    T_TYPE,
    T_ID("[a-zA-Z_][a-zA-Z0-9_]*"),
    T_WHITESPACE(" +");

    var regex: Pattern? = null

    constructor() {
    }

    constructor(regex: String) {
        this.regex = Pattern.compile(regex)
    }

    companion object {
        fun consume(type: Tokens, cursor: Cursor): Token {
            T_WHITESPACE.consume(cursor)
            return type.consume(cursor) ?: throw IllegalStateException("Syntax error at '${cursor.remainingString.substring(0, 10)}' expected token ${type}!")
        }

        fun nextToken(cursor: Cursor): Token? {
            for (type in values) {
                var token = type.consume(cursor)
                if (token != null) {
                    return token
                }
            }
            return null
        }

        private fun consumeRegex(cursor: Cursor, regex: Pattern): String? {
            val source = cursor.remainingString
            var found: Boolean = false
            var match: String? = null
            for (i in 1..source.length) {
                val potentialToken = source.substring(0, i)
                if (regex.matcher(potentialToken).matches()) {
                    found = true
                    match = potentialToken
                } else if (found) {
                    return match
                }
            }
            return match
        }

        private fun consumeRegexToken(type: Tokens, cursor: Cursor, pattern: Pattern): Token? {
            var token = consumeRegex(cursor, pattern)
            if (token != null) {
                cursor.advance(token.length)
                return Token(type, token)
            } else {
                return null
            }
        }
    }

    private fun consume(cursor: Cursor): Token? {
        if (regex != null) {
            return consumeRegexToken(this, cursor, regex as Pattern)
        }
        return null
    }

}