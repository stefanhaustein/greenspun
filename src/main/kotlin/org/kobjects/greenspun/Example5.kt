package org.kobjects.greenspun

fun main() {

    val fizzBuzz = block {
        val count = Var(20)
        While (count) {
            If (count % 5) {
                If (count % 3) {
                    PrintLn(count)
                }.Else {
                    PrintLn("Fizz")
                }
            }.Else {
                If (count % 3) {
                    PrintLn("FizzBuzz")
                }.Else {
                    PrintLn("Buzz")
                }
            }
            Set(count, count - 1)
        }
    }
    fizzBuzz.eval(Context(2))
}