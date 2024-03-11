package util.data.distance

//import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

//import org.junit.jupiter.api.Test;

class DistanceCalculatorTest {

    private val Eps: Double = 0.0001

    @Test
    fun testAcc0() {
        val distanceCalculator: DistanceCalculator = DistanceCalculator()
        val accValues: Array<Double> = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,)
        val timestamp: Array<Long> = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val resultExpect: Double = 0.0
        for (i in 0..9) {
            distanceCalculator.getDistance(accValues[i], timestamp[i])
        }

        assertTrue(abs(resultExpect -  distanceCalculator.getDistance(0.0, 10)) < Eps )
    }
}