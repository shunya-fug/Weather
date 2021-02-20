package com.example.weather

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // moshi: JSONパーサー
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val apiKey = intent.getStringExtra("apiKey")
        // Fuel: HTTP通信ライブラリ
        Fuel.get("weather", listOf("appid" to apiKey)).responseString { request, _, result ->
            when (result) {
                is Result.Failure -> {
                    Log.d("Request", request.toString())
                    val ex = result.getException()
                    ex.printStackTrace()
                }
                is Result.Success -> {
                    val data = result.get()
                    // API からの戻り値は WeatherFields データクラスに格納する
                    val weatherFields = moshi.adapter(WeatherFields::class.java).fromJson(data)
                    weather.text = weatherFields!!.weather.first().description
                }
            }
        }
    }
}
