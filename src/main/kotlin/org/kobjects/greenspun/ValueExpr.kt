package org.kobjects.greenspun

class ValueExpr(val value: Any) : Expr {
    override fun eval(context: Context) = value
}