package util.data.distance

/**
 * calculate distance from instantaneous acceleration
 */
class  DistanceCalculator {
    data class Acceleration (val acceleration: Double, val timestamp: Long)
    data class Velocity (val speed: Double, val timestamp: Long)

    private var distance: Double = 0.0
    private var accelerationPrevious: Acceleration? = null
    private var accelerationNow: Acceleration? = null
//    private var speedPrevious: Speed? = null
//    private var speedNow: Speed? = null
    private var velocityPrevious: Double = 0.0
//    private var velocityDelta: Double = 0.0
    private fun getVelocityDelta(): Double {
        val velocityDelta =
            (accelerationNow!!.acceleration + accelerationPrevious!!.acceleration) / 2 * (accelerationNow!!.timestamp - accelerationPrevious!!.timestamp) / 1E9
        return velocityDelta
    }

    fun addAcceleration(acceleration: Double, timestamp: Long): Unit {
        //check if it's the first measurement of acceleration. If true, return distance == 0
        if (accelerationNow == null) {
            accelerationNow = Acceleration(acceleration, timestamp)
            return
        }
        accelerationPrevious = accelerationNow
        accelerationNow = Acceleration(acceleration, timestamp)
        //check that all speeds are there after set. If not return distance == 0
        val velocityDelta: Double = this.getVelocityDelta()
        val distanceDelta: Double = (velocityPrevious + (velocityPrevious + velocityDelta) ) / 2 * (accelerationNow!!.timestamp - accelerationPrevious!!.timestamp) / 1E9
        velocityPrevious += velocityDelta
        if (velocityDelta<0.1) {
            velocityPrevious = velocityPrevious / 2
        }
        distance += distanceDelta

    }

    /**
     * @return distance in meters
     */
    fun getDistance(): Double {
        return this.distance
    }

    fun setToDefault(): Unit {
        accelerationNow = null
        velocityPrevious = 0.0
        distance = 0.0
    }

}