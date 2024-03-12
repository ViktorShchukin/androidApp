package util.data.distance

//import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

//import org.junit.jupiter.api.Test;

class DistanceCalculatorTest {

    private val Eps: Double = 0.01

    @Test
    fun testAcc0() {
        val distanceCalculator: DistanceCalculator = DistanceCalculator()
        val accValues: Array<Double> = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,)
        val timestamp: Array<Long> = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val resultExpect: Double = 0.0
        for (i in 0..9) {
            distanceCalculator.addAcceleration(accValues[i], timestamp[i])
        }

        assertTrue(abs(resultExpect -  distanceCalculator.getDistance()) < Eps )
    }

    @Test
    fun testAcc1() {
        val distanceCalculator: DistanceCalculator = DistanceCalculator()
        val accValues: Array<Double> = arrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        val timestamp: Array<Long> = arrayOf(0, 1000000000, 2000000000, 3000000000, 4000000000, 5000000000, 6000000000, 7000000000, 8000000000, 9000000000, 10000000000)
        val resultExpect: Double = 50.0
        for (i in 0..10) {
            distanceCalculator.addAcceleration(accValues[i], timestamp[i])
        }

        assertTrue(abs(resultExpect -  distanceCalculator.getDistance()) < Eps )
    }

    @Test
    fun testAcc05() {
        val distanceCalculator: DistanceCalculator = DistanceCalculator()
        val accValues: Array<Double> = arrayOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1)
        val timestamp: Array<Long> = arrayOf(0, 1_000_000_000, 2000000000, 3000000000, 4000000000, 5000000000, 6000000000, 7000000000, 8000000000, 9000000000, 10000000000)
        val resultExpect: Double = 5.0
        for (i in 0..10) {
            distanceCalculator.addAcceleration(accValues[i], timestamp[i])
        }

        assertTrue(abs(resultExpect -  distanceCalculator.getDistance()) < Eps )
    }

    @Test
    fun testAccMinus1() {
        val distanceCalculator: DistanceCalculator = DistanceCalculator()
        val accValues: Array<Double> = arrayOf(-1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0)
        val timestamp: Array<Long> = arrayOf(0, 1000000000, 2000000000, 3000000000, 4000000000, 5000000000, 6000000000, 7000000000, 8000000000, 9000000000, 10000000000)
        val resultExpect: Double = -50.0
        for (i in 0..10) {
            distanceCalculator.addAcceleration(accValues[i], timestamp[i])
        }

        assertTrue(abs(resultExpect -  distanceCalculator.getDistance()) < Eps )
    }

    @Test
    fun testAccWithJerk1() {
        val distanceCalculator: DistanceCalculator = DistanceCalculator()
        val accValues: Array<Double> = arrayOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
        val timestamp: Array<Long> = arrayOf(0, 1000000000, 2000000000, 3000000000, 4000000000, 5000000000, 6000000000, 7000000000, 8000000000, 9000000000, 10000000000)
        val resultExpect: Double = 1000.0/6.0
        for (i in 0..10) {
            distanceCalculator.addAcceleration(accValues[i], timestamp[i])
        }

        assertTrue(abs(resultExpect -  distanceCalculator.getDistance())/resultExpect < Eps )
    }
}