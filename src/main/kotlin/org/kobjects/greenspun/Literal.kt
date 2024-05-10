package org.kobjects.greenspun

class Literal(val value: Any) : Expr {
    override fun eval(context: Context) = value

    override fun toString() = "Literal($value)"
}