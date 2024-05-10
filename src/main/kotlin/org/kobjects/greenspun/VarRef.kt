package org.kobjects.greenspun

class VarRef(val index: Int) : Expr {
    override fun eval(context: Context) = context.variables[index]

    override fun toString() = "var$index"
}