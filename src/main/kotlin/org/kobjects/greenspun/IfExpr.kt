package org.kobjects.greenspun

class IfExpr(
    val condition: Expr,
    val then: Block,
    var otherwise: Block = Block(emptyList())
) : Expr{
    override fun eval(context: Context) {
        if (condition.evalBoolean(context)) {
            then.eval(context)
        } else {
            otherwise?.eval(context)
        }
    }
}