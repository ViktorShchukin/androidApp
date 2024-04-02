package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.model.Data
import com.example.test.model.Experiment
import com.example.test.tools.HTTPClient
import com.example.test.ui.theme.TestTheme
import util.data.distance.DistanceCalculator
import util.data.filter.KalmanFilter
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

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
    private lateinit var experiment: Experiment
    private val dataList: MutableList<Data> = mutableListOf()

    var experimentName: AtomicReference<String> = AtomicReference("row_data_100_2_")
//    var experimentName2 by rememberSaveable {
//        mutableStateOf("")
//    }
        

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

//        x = dfkX.correct(event.values[0].toDouble())
//        y = dfkY.correct(event.values[1].toDouble())
//        z = dfkZ.correct(event.values[2].toDouble())
        x = event.values[0].toDouble()
        y = event.values[1].toDouble()
        z = event.values[2].toDouble()
//        if (x < 0.04 && x > -0.04) x=0.0
//        if (y < 0.04 && y > -0.04) y=0.0
//        if (z < 0.3 && z > -0.3) z=0.0
        lastAccelerometerUpdateTime = event.timestamp
        distCalcX.addAcceleration(x, lastAccelerometerUpdateTime)
        distCalcY.addAcceleration(y, lastAccelerometerUpdateTime)
        distCalcZ.addAcceleration(z, lastAccelerometerUpdateTime)
        this.drawPage()
        if (experimentIsActive) {
            val data = Data(x, y, z, lastAccelerometerUpdateTime)
            dataList.add(data)
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
                                            experimentName = this.experimentName,
                                            buttonFunctionStart = this::startExperiment,
                                            buttonFunctionEnd = this::endExperiment,
                                            experimentState = this.experimentIsActive)
                }
            }
        }
    }

//    @SuppressLint("HardwareIds")
    private fun startExperiment(){
        experimentIsActive = true
        experiment = Experiment(name = experimentName.get(),
                                phoneBrand = Build.BRAND,
                                phoneModel = Build.MODEL,
                                phoneSerial = "",
                                exTimestamp = Instant.ofEpochMilli(System.currentTimeMillis()).toString(),
                                comment = "row data, distance: 100 cm") //todo how to create name for experiment


    }

    private fun endExperiment(){
        experimentIsActive = false
        client.post(experiment, dataList)
        dataList.removeAll(this.dataList)
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
                           experimentName: AtomicReference<String>,
                           experimentState: Boolean,
                           buttonFunctionStart: () -> Unit,
                           buttonFunctionEnd: () -> Unit,) {
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
            Text(text = "experiment is active: $experimentState")
            TextField(
                value = experimentName.get(),
                onValueChange = {experimentName.set(it)},
                label = { Text(text = "experiment name")}
                )

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