package org.kobjects.greenspun

class WhileExpr(
    val condition: Expr,
    val body: Block
) : Expr {
    override fun eval(context: Context) {
        while (condition.evalBoolean(context)) {
            body.eval(context)
        }
    }
}

