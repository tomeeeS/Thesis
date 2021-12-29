package tmit.bme.telkicar.logic

import kotlin.math.abs

class LogicHelper {

    companion object {
        private const val doubleComparisonThreshold = 1e-6

        fun isDoubleEquals(d1: Double, d2: Double) = abs(d1 - d2) < doubleComparisonThreshold
    }
}