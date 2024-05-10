package org.kobjects.greenspun

class BinaryOperator(
    val op: Char,
    val leftExpr: Expr,
    val rightExpr: Expr
): Expr {
    override fun eval(context: Context): Any {
        val l = (leftExpr.eval(context) as Number).toDouble()
        val r = (rightExpr.eval(context) as Number).toDouble()
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