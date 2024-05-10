package org.kobjects.greenspun

interface Expr {
    fun eval(context: Context): Any

    operator fun plus(other: Any) = BinaryOperator('+', this, Expr.of(other))

    companion object {
        fun of(value: Any): Expr = if (value is Expr) value else Literal(value)
    }
}