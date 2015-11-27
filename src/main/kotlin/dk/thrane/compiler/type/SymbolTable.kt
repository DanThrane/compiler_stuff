package dk.thrane.compiler.type

import java.util.*

class SymbolTable {
    private val symbols: MutableMap<String, Symbol> = HashMap()
    var parent: SymbolTable? = null
        private set

    fun putSymbol(name: String, type: Type) {
        symbols.put(name, Symbol(name, type))
    }

    fun getNoTraversal(name: String) = symbols[name]
    fun getSymbolAndLevels(name: String): Pair<Symbol, Int>? {
        var currentTable: SymbolTable? = this
        var level = 0
        while (currentTable != null) {
            val symbol = currentTable.getNoTraversal(name)
            if (symbol != null) {
                return Pair(symbol, level)
            }
            level++
        }
        return null
    }

    fun scopeSymbolTable(): SymbolTable {
        val symbolTable = SymbolTable()
        symbolTable.parent = this
        return symbolTable
    }
}
