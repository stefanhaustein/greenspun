package org.kobjects.greenspun

fun main() {
    val program = block {
        PrintLn(Literal("Hello World"))
        PrintLn(Literal(40) + Literal(2))
    }

    println(program)
    program.eval(Context())
}