package org.kobjects.greenspun

fun main() {
    val program = block {
        val myVar = Var(Literal(40))
        Set(myVar, myVar + Literal(2))
        PrintLn(myVar)
    }

    println(program)
    program.eval(Context(1))
}