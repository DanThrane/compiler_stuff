package dk.thrane.compiler.type

import java.util.*

sealed class Scope {
    private val symbols: MutableMap<String, Symbol> = HashMap()

    var parent: Scope? = null
        private set

    operator fun set(name: String, type: Type) {
        symbols[name] = Symbol(name, type)
    }

    operator fun get(name: String): Symbol = symbols[name]!!

    fun getOrNull(name: String): Symbol? = symbols[name]

    fun getSymbolAndLevelsOrNull(name: String): Pair<Symbol, Int>? {
        var currentScope: Scope? = this
        var level = 0
        while (currentScope != null) {
            val symbol = currentScope.getOrNull(name)
            if (symbol != null) {
                return Pair(symbol, level)
            }
            currentScope = currentScope.parent
            level++
        }
        return null
    }

    fun getSymbolAndLevels(name: String): Pair<Symbol, Int> = getSymbolAndLevelsOrNull(name)!!

    fun <T : Scope> scopeSymbolTable(companion: ScopeCompanion<T>): T {
        val scope = companion.factory()
        scope.parent = this
        return scope
    }
}

interface ScopeCompanion<T : Scope> {
    val factory: () -> T
}

// We cannot create more than one global scope
class GlobalScope : Scope()

class FunctionScope : Scope() {
    lateinit var function: TypeFunction

    companion object : ScopeCompanion<FunctionScope> {
        override val factory: () -> FunctionScope = { FunctionScope() }
    }
}

class RecordScope : Scope() {
    companion object : ScopeCompanion<RecordScope> {
        override val factory: () -> RecordScope = { RecordScope() }
    }
}
