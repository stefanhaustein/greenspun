package org.kobjects.greenspun

fun main() {
    val program = block {
        val myVar = Var(40)
        Set(myVar, myVar + 2)
        PrintLn(myVar)
    }

    println(program)
    program.eval(Context(1))
}