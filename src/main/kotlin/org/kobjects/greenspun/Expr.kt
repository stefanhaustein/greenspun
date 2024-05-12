package org.kobjects.greenspun

interface Expr {
    fun eval(context: Context): Any

    fun evalDouble(context: Context) = (eval(context) as Number).toDouble()

    fun evalBoolean(context: Context): Boolean {
        val v = eval(context)
        return v == true || (v is Number && v.toDouble() != 0.0)
    }

    operator fun plus(other: Any) = BinaryOperator('+', this, Expr.of(other))
    operator fun times(other: Any) = BinaryOperator('*', this, Expr.of(other))
    operator fun rem(other: Any) = BinaryOperator('%', this, Expr.of(other))
    operator fun minus(other: Any) = BinaryOperator('-', this, Expr.of(other))

    companion object {
        fun of(value: Any): Expr = if (value is Expr) value else Literal(value)
    }
}