package org.kobjects.greenspun


fun program(init: ProgramBuilder.() -> Unit): () -> Any {
    val builder = ProgramBuilder()
    init(builder)
    val funRef = builder.lastFunRef
    require(funRef != null) {
        "Program must declare at least one function"
    }
    val invocation = funRef.invoke()
    return { invocation.eval(Context()) }
}

class ProgramBuilder() {
    var lastFunRef: FunRef? = null

    fun Fun(init: FunBuilder.() -> Unit): FunRef {
        val builder = FunBuilder()
        init(builder)
        val funRef = FunRef(builder.paramCount, builder.variables.size, Block(builder.statements))
        lastFunRef = funRef
        return funRef
    }
}