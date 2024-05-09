package org.kobjects.greenspun

class BlockBuilder {
    var statements = mutableListOf<Expr>()

    fun Print(expr: Expr) {
        statements.add(
            object : Expr {
                override fun eval(context: Context) {
                    println(expr.eval(context))
                }
            }
        )
    }

    fun build() = Block(statements.toList())
}