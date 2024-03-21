package com.example.test

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.tools.HTTPClient
import com.example.test.ui.theme.TestTheme
import util.data.distance.DistanceCalculator
import util.data.filter.KalmanFilter

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor

    private var accuracy: Int = 0
    private var lastAccelerometerUpdateTime: Long = 0
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0

    private val dfkX = KalmanFilter(2.0, 15.0, 1.0, 1.0)
    private val dfkY = KalmanFilter(2.0, 15.0, 1.0, 1.0)
    private val dfkZ = KalmanFilter(2.0, 15.0, 1.0, 1.0)

    private val distCalcX = DistanceCalculator()
    private val distCalcY = DistanceCalculator()
    private val distCalcZ = DistanceCalculator()

    private val client = HTTPClient()
    private val clientText = client.get()

    private var experimentIsActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //todo what is better? NPE(!!) or other exception? Maybe custom exception?
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!! //?: throw  IllegalArgumentException("")
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        this.drawPage()

//        val webView: WebView = WebView(this)
//        webView.settings.javaScriptEnabled = true
//        setContentView(webView)
//        webView.loadUrl("https://www.google.com")

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) {
            return;
        }
        if (event != null) {
//            x = dfkX.correct(event.values[0].toDouble())
//            y = dfkY.correct(event.values[1].toDouble())
//            z = dfkZ.correct(event.values[2].toDouble())
            x = event.values[0].toDouble()
            y = event.values[1].toDouble()
            z = event.values[2].toDouble()
            if (x < 0.04 && x > -0.04) x=0.0
            if (y < 0.04 && y > -0.04) y=0.0
            if (z < 0.3 && z > -0.3) z=0.0
            lastAccelerometerUpdateTime = event.timestamp
            distCalcX.addAcceleration(x, lastAccelerometerUpdateTime)
            distCalcY.addAcceleration(y, lastAccelerometerUpdateTime)
            distCalcZ.addAcceleration(z, lastAccelerometerUpdateTime)
            this.drawPage()
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        this.accuracy = accuracy
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun drawPage() {
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DrawAccelerometerValue(timeStamp = lastAccelerometerUpdateTime,
                                            x = this.x,
                                            y = this.y,
                                            z = this.z,
                                            distX = this.distCalcX,
                                            distY = this.distCalcY,
                                            distZ = this.distCalcZ,
                                            client = this.client.getResponseBody(),
                                            buttonFunctionStart = this::startExperiment,
                                            buttonFunctionEnd = this::endExperiment)
                }
            }
        }
    }

    private fun startExperiment(){
        experimentIsActive = true

    }

    private fun endExperiment(){
        experimentIsActive = false
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface (color = Color.Magenta) {
        Text(
            text = "Hello $name!",
            modifier = modifier.padding(24.dp)
        )
    }
}

@Composable
fun DrawAccelerometerValue(timeStamp: Long, x:Double, y:Double, z:Double,
                           distX: DistanceCalculator,
                           distY: DistanceCalculator,
                           distZ: DistanceCalculator,
                           client: String,
                           buttonFunctionStart: () -> Unit,
                           buttonFunctionEnd: () -> Unit) {
    Surface {
        Column {
            Text(text = "[ $timeStamp ]")
            Text(text = "acceleration x: $x")
            Text(text = "acceleration y: $y")
            Text(text = "acceleration z: $z")
            Text(text = "distance x: ${distX.getDistance()}")
            Text(text = "distance y: ${distY.getDistance()}")
            Text(text = "distance z: ${distZ.getDistance()}")
            Text(text = "client test: $client")

            Button(onClick = buttonFunctionStart) {
                Text(text = "Start experiment")
            }
            Button(onClick = buttonFunctionEnd) {
                Text(text = "End experiment")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestTheme {
        Greeting("Android")
    }
}

fun nanosToSecond(nonos: Long): Double {
    return nonos / 1E9
}