package org.kobjects.greenspun

fun block(init: BlockBuilder.() -> Unit): Block {
    val builder = BlockBuilder(mutableListOf())
    init(builder)
    return Block(builder.statements)
}

open class BlockBuilder(val variables: MutableList<VarRef>) {
    var statements = mutableListOf<Expr>()

    fun Var(initialValue: Any): VarRef {
        val varRef = VarRef(variables.size)
        variables.add(varRef)
        statements.add(VarAssignment(true, varRef.index, Expr.of(initialValue)))
        return varRef
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
        val blockBuilder = BlockBuilder(variables)
        init(blockBuilder)
        val ifExpr = IfExpr(Expr.of(condition), Block(blockBuilder.statements))
        statements.add(ifExpr)
        return Elseable(ifExpr, statements.size - 1)
    }

    inner class Elseable(val ifExpr: IfExpr, val index: Int) {
        fun Else(init: BlockBuilder.() -> Unit) {
            val builder = BlockBuilder(variables)
            init(builder)
            statements.set(index, IfExpr(ifExpr.condition, ifExpr.then, Block(builder.statements)))
        }
    }

    fun While(condition: Any, init: BlockBuilder.() -> Unit) {
        val builder = BlockBuilder(variables)
        init(builder)
        statements.add(WhileExpr(Expr.of(condition), Block(builder.statements)))
    }

    operator fun Expr.unaryPlus() {
        statements.add(this)
    }
}