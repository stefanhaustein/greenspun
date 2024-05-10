package org.kobjects.greenspun

fun block(init: BlockBuilder.() -> Unit): Block {
    val builder = BlockBuilder()
    init(builder)
    return builder.build()
}

class BlockBuilder(var varCount: Int = 0) {
    var statements = mutableListOf<Expr>()

    fun Var(initialValue: Expr): VarRef {
        val index = varCount++
        statements.add(VarAssignment(true, index, initialValue))
        return VarRef(index)
    }

    fun Set(target: VarRef, value: Expr) {
        statements.add(VarAssignment(false, target.index, value))
    }

    fun PrintLn(expr: Expr) {
        statements.add(
            object : Expr {
                override fun eval(context: Context) {
                    println(expr.eval(context))
                }

                override fun toString() = "PrintLn($expr)"
            }
        )
    }

    fun build() = Block(statements.toList())
}