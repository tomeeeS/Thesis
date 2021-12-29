package tmit.bme.telkicar.logic.matching.ip

import net.sf.javailp.*

class LpSolveIpSolver : IpSolver<String> {

    private lateinit var lpModel: Problem
    private lateinit var solver: Solver
    private lateinit var cost: Linear
    private lateinit var actualConstraint: Linear
    private lateinit var constraintType: ConstraintType
    private lateinit var result: Result

    override val variablesCount: Int
        get() = lpModel.variablesCount

    override val constraintsCount: Int
        get() = lpModel.constraintsCount

    override fun init() {
        lpModel = Problem()
        solver = SolverFactoryLpSolve().get()
        cost = Linear()
    }

    override fun addCostBoolVar(variableRef: String, coefficient: Number) {
        cost.add(coefficient, variableRef)
        lpModel.setVarType(variableRef, VarType.BOOL)
    }

    override fun startConstraint(constraintType: ConstraintType) {
        actualConstraint = Linear()
        this.constraintType = constraintType
    }

    override fun addConstraintVariable(variableRef: String, coefficient: Number) {
        actualConstraint.add(coefficient, variableRef)
    }

    override fun registerConstraint(rhs: Number) {
        val constraintTypeString = when(constraintType) {
            ConstraintType.Eq -> "="
            ConstraintType.Lt -> "<="
            ConstraintType.Gt -> ">="
        }
        lpModel.add(actualConstraint, constraintTypeString, rhs)
    }

    override fun solve(): Any {
        lpModel.setObjective(cost, OptType.MAX)
        result = solver.solve(lpModel)
        return result
    }

    override fun getResult(variableRef: String): Int = result.get(variableRef).toInt()
}