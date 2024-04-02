package com.example.test.tools

import com.example.test.model.Data
import com.example.test.model.Experiment
import com.example.test.tools.dto.ExperimentWithDataDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.util.concurrent.atomic.AtomicReference

class HTTPClient {

    private val BASE_PATH: String = "http://192.168.69.161:8080/imu/experiment"
    private val client: OkHttpClient = OkHttpClient()
    private val responseBody: AtomicReference<String> = AtomicReference("")
    private val MEDIA_TYPE_JSON: MediaType = "application/json".toMediaType()

    private val mapper = jacksonObjectMapper()
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

                responseBody.set("success") //.set(response.body!!.string())
            }
        })
        }

    fun post(experiment: Experiment, dataList: List<Data>){
        val dto = ExperimentWithDataDTO(experiment, dataList) //todo end this. reformat experiment and dataList to ExperimentWithDataDTO
        val requestBody = mapper.writeValueAsString(dto)
        val request = Request.Builder()
            .url("$BASE_PATH/withdata")
            .post(requestBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                    responseBody.set("success2")
                }
            }

            override fun onFailure(call: Call, e: java.io.IOException) {
//                e.printStackTrace()
                responseBody.set("fail: $e")
            }
        })

    }

    public fun getResponseBody():String{
        return responseBody.get()
    }

    }
