package org.kobjects.greenspun

class VarAssignment(
    val declaration: Boolean,
    val index: Int,
    val value: Expr
) : Expr {
    override fun eval(context: Context) {
        context.variables[index] = value.eval(context)
    }

    override fun toString() =
        if (declaration) "val var$index = $value" else "Set(var$index, $value)"
}