package com.example.weather

import android.app.Application
import com.github.kittinunf.fuel.core.FuelManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val lat = "33"
        val lon = "130"

        FuelManager.instance.basePath = "http://api.openweathermap.org/data/2.5/"
        FuelManager.instance.baseParams = listOf(
            "lat" to lat,
            "lon" to lon,
            "units" to "metric", // WeatherFields.main.temp をセルシウス温度で取得
            "lang" to "ja"
        )
    }
}