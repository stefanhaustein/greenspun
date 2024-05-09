package org.kobjects.greenspun

interface Expr {
    fun eval(context: Context): Any

    companion object {
        fun of(value: Any): Expr = if (value is Expr) value else ValueExpr(value)
    }
}