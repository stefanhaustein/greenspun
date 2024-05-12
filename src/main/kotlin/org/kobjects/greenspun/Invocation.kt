package org.kobjects.greenspun

class Invocation(val funRef: FunRef, val args: List<Expr>) : Expr {

    init {
        require(funRef.paramCount == args.size) {
            "Parameter count mismatch; expected: ${funRef.paramCount}; actual: ${args.size}"
        }
    }

    override fun eval(context: Context): Any {
        val funContext = Context(funRef.varCount)
        for (i in 0 until args.size) {
            funContext.variables[i] = args[i].eval(context)
        }
        return funRef.body.eval(funContext)
    }
}