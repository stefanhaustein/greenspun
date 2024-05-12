package org.kobjects.greenspun

class FunRef(
    val paramCount: Int,
    val varCount: Int,
    val body: Block) {

    operator fun invoke(vararg args: Any) = Invocation(this, args.map { Expr.of(it) })
}