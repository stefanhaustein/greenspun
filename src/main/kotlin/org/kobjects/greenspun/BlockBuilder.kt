package org.kobjects.greenspun

fun block(init: BlockBuilder.() -> Unit): Block {
    val builder = BlockBuilder(BuildContext())
    init(builder)
    return builder.build()
}

class BlockBuilder(val buildContext: BuildContext) {
    var statements = mutableListOf<Expr>()

    fun Var(initialValue: Any): VarRef {
        val index = buildContext.varCount++
        statements.add(VarAssignment(true, index, Expr.of(initialValue)))
        return VarRef(index)
    }

    fun Set(target: VarRef, value: Any) {
        statements.add(VarAssignment(false, target.index, Expr.of(value)))
    }

    fun PrintLn(value: Any) {
        val expr = Expr.of(value)
        statements.add(
            object : Expr {
                override fun eval(context: Context) {
                    println(expr.eval(context))
                }

                override fun toString() = "PrintLn($expr)"
            }
        )
    }

    fun If(condition: Any, init: BlockBuilder.() -> Unit): Elseable {
        val blockBuilder = BlockBuilder(buildContext)
        init(blockBuilder)
        val ifExpr = IfExpr(Expr.of(condition), blockBuilder.build())
        statements.add(ifExpr)
        return Elseable(ifExpr, statements.size - 1)
    }

    inner class Elseable(val ifExpr: IfExpr, val index: Int) {
        fun Else(init: BlockBuilder.() -> Unit) {
            val builder = BlockBuilder(buildContext)
            init(builder)
            statements.set(index, IfExpr(ifExpr.condition, ifExpr.then, builder.build()))
        }
    }

    fun While(condition: Any, init: BlockBuilder.() -> Unit) {
        val blockBuilder = BlockBuilder(buildContext)
        init(blockBuilder)
        statements.add(WhileExpr(Expr.of(condition), blockBuilder.build()))
    }

    fun build() = Block(statements.toList())
}