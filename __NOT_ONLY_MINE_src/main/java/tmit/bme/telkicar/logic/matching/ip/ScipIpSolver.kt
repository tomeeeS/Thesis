package tmit.bme.telkicar.logic.matching.ip

import com.google.ortools.linearsolver.MPObjective
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable


class ScipIpSolver : IpSolver<String> {

    private lateinit var solver: MPSolver
    private lateinit var objective: MPObjective
    private lateinit var constraintType: ConstraintType
    private lateinit var constraintVariables: MutableMap<MPVariable, Double>

    override val variablesCount: Int
        get() = solver.numVariables()

    override val constraintsCount: Int
        get() = solver.numConstraints()

    override fun init() {
        System.loadLibrary("jniortools")
        System.loadLibrary("ortools")
        solver = MPSolver.createSolver("SCIP")
        objective = solver.objective().apply { setMaximization() }
        constraintVariables = mutableMapOf()
    }

    override fun addCostBoolVar(variableRef: String, coefficient: Number) {
        val variable = solver.makeBoolVar(variableRef)
        objective.setCoefficient(variable, coefficient.toDouble());
    }

    override fun startConstraint(constraintType: ConstraintType) {
        this.constraintType = constraintType
        constraintVariables.clear()
    }

    override fun addConstraintVariable(variableRef: String, coefficient: Number) {
        constraintVariables[solver.lookupVariableOrNull(variableRef)] = coefficient.toDouble()
    }

    override fun registerConstraint(rhs: Number) {
        val constraint = when(constraintType) {
            ConstraintType.Eq ->
                solver.makeConstraint(
                    rhs.toDouble() - doubleComparisonThreshold, rhs.toDouble() + doubleComparisonThreshold)
            ConstraintType.Lt ->
                solver.makeConstraint(Double.NEGATIVE_INFINITY, rhs.toDouble() + doubleComparisonThreshold)
            ConstraintType.Gt ->
                solver.makeConstraint(rhs.toDouble() - doubleComparisonThreshold, Double.POSITIVE_INFINITY)
        }
        constraintVariables.forEach { (variable, coeff) ->
            constraint.setCoefficient(variable, coeff)
        }
    }

    override fun solve(): String {
        solver.solve()
        return solver.createSolutionResponseProto().toString()
    }

    override fun getResult(variableRef: String) = solver.lookupVariableOrNull(variableRef).solutionValue().toInt()

    companion object {
        const val doubleComparisonThreshold = 1e-6
    }
}