package org.kobjects.greenspun


class FunBuilder : BlockBuilder(mutableListOf()) {

    var paramCount = 0

    fun Param(): VarRef {
        require(statements.isEmpty()) {
            "Parameters must be declared before all other statements"
        }
        val varRef = VarRef(paramCount++)
        variables.add(varRef)
        return varRef
    }
}