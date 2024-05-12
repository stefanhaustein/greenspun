package org.kobjects.greenspun

fun main() {
    val program = program {
        val sqr = Fun() {
            val x = Param()
            +(x * x)
        }

        Fun() {
            PrintLn(sqr(5))
        }
    }

    program()
}