package tmit.bme.telkicar.logic.matching.ip

enum class ConstraintType {
    Eq,
    Lt,
    Gt
}

interface IpSolver<VariableRef> {
    val variablesCount: Int
    val constraintsCount: Int

    fun init()

    fun addCostBoolVar(variableRef: VariableRef, coefficient: Number)

    fun startConstraint(constraintType: ConstraintType)
    fun addConstraintVariable(variableRef: VariableRef, coefficient: Number)
    fun registerConstraint(rhs: Number)

    fun solve(): Any
    fun getResult(variableRef: VariableRef): Int
}
