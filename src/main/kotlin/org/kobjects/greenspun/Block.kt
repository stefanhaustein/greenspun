package org.kobjects.greenspun

class Block(val statements: List<Expr>) : Expr {
    override fun eval(context: Context): Any {
        var result: Any = Unit
        for (statement in statements) {
            result = statement.eval(context)
        } 
        return result
    }
}