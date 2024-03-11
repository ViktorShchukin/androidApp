package util.data.distance

/**
 * calculate distance from instantaneous acceleration
 */
class  DistanceCalculator {
    data class Acceleration (val acceleration: Double, val timestamp: Long)
    data class Speed (val speed: Double, val timestamp: Long)

    private var distance: Double = 0.0
    private var accelerationPrevious: Acceleration? = null
    private var accelerationNow: Acceleration? = null
    private var speedPrevious: Speed? = null
    private var speedNow: Speed? = null

    private fun setSpeed() {
        val speedCalculated =
            (accelerationNow!!.acceleration + accelerationPrevious!!.acceleration) / 2 * (accelerationNow!!.timestamp - accelerationPrevious!!.timestamp) * 1E9
        speedPrevious = speedNow
        speedNow = Speed(speedCalculated, accelerationNow!!.timestamp)

    }

    /**
     * @return distance in meters
     */
    fun getDistance(acceleration: Double, timestamp: Long): Double {
        //check if it's the first measurement of acceleration. If true, return distance == 0
        if (accelerationNow == null) {
            accelerationNow = Acceleration(acceleration, timestamp)
            return distance
        }
        accelerationPrevious = accelerationNow
        accelerationNow = Acceleration(acceleration, timestamp)
        this.setSpeed()
        //check that all speeds are there after set. If not return distance == 0
        if (speedNow != null && speedPrevious != null) {
            val distNow: Double = (speedNow!!.speed + speedPrevious!!.speed) / 2 * (speedNow!!.timestamp - speedPrevious!!.timestamp) * 1E9
            distance += distNow
            return distance
        }
        return distance
    }

}