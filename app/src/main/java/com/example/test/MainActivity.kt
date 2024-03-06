package com.example.test

import android.os.Bundle
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.test.ui.theme.TestTheme
import data.utils.KalmanFilter

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor

    private var accuracy: Int = 0
    private var lastAccelerometerUpdateTime: Long = 0
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0

    private val dfkX = KalmanFilter(2.0, 15.0, 1.0, 1.0,)
    private val dfkY = KalmanFilter(2.0, 15.0, 1.0, 1.0,)
    private val dfkZ = KalmanFilter(2.0, 15.0, 1.0, 1.0,)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //todo what is better? NPE(!!) or other exception? Maybe custom exception?
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!! //?: throw  IllegalArgumentException("")
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        this.drawPage()

//        val webView: WebView = WebView(this)
//        webView.settings.javaScriptEnabled = true
//        setContentView(webView)
//        webView.loadUrl("https://www.google.com")

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            x = dfkX.correct(event.values[0].toDouble())
            y = dfkY.correct(event.values[1].toDouble())
            z = dfkZ.correct(event.values[2].toDouble())
            lastAccelerometerUpdateTime = event.timestamp
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
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun drawPage() {
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DrawAccelerometerValue(timeStamp = lastAccelerometerUpdateTime,
                                            x = this.x,
                                            y = this.y,
                                            z = this.z)
                }
            }
        }
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
fun DrawAccelerometerValue(timeStamp: Long, x:Double, y:Double, z:Double) {
    Surface {
        Column {
            Text(text = "[ $timeStamp ]")
            Text(text = "x: $x")
            Text(text = "y: $y")
            Text(text = "z: $z")
        }
//        Text(text = "[ $timeStamp ] x: $x , y: $y, z: $z")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestTheme {
        Greeting("Android")
    }
}