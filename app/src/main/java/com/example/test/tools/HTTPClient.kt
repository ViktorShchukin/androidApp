package com.example.test.tools

import com.example.test.model.Data
import com.example.test.model.Experiment
import com.example.test.tools.dto.ExperimentWithDataDTO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.util.concurrent.atomic.AtomicReference

class HTTPClient {

    private val BASE_PATH: String = "http://10.0.2.2:8080/imu/experiment"
    private val client: OkHttpClient = OkHttpClient()
    private val responseBody: AtomicReference<String> = AtomicReference("")

    private val moshi: Moshi = Moshi.Builder().build()
    private val DTOadapter: JsonAdapter<ExperimentWithDataDTO> = moshi.adapter(ExperimentWithDataDTO::class.java)

    fun get(){
        val request = Request.Builder()
            .url(BASE_PATH)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                responseBody.set(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if(!response.isSuccessful) throw IOException("Unexpected code $response")

                responseBody.set(response.body!!.string())
            }
        })
        }

    fun post(experiment: Experiment, dataList: List<Data>){
        var dto = ExperimentWithDataDTO(experiment, dataList) //todo end this. reformat experiment and dataList to ExperimentWithDataDTO
        var requestBody = DTOadapter.toJson(dto)
        val request = Request.Builder()
            .url("$BASE_PATH/withdata")
            .post(requestBody.toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                }
            }

            override fun onFailure(call: Call, e: java.io.IOException) {
                e.printStackTrace()
            }
        })

    }

    public fun getResponseBody():String{
        return responseBody.get()
    }

    }