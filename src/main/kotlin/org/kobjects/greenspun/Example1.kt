package org.kobjects.greenspun

fun main() {
    val addExpr = Literal(40) + Literal(2)

    println(addExpr)
    println(addExpr.eval(Context()))
}