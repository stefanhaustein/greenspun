package org.kobjects.greenspun

class BinaryOperator(
    val op: Char,
    val leftExpr: Expr,
    val rightExpr: Expr
): Expr {
    override fun eval(context: Context): Any {
        val l = leftExpr.evalDouble(context)
        val r = rightExpr.evalDouble(context)
        return when (op) {
            '+' -> l + r
            '*' -> l * r
            '-' -> l - r
            '/' -> l / r
            '%' -> l % r
            // ...
            else -> throw UnsupportedOperationException(op.toString())
        }
    }

    override fun toString() = "($leftExpr $op $rightExpr)"
}