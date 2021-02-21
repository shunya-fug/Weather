package com.example.weather

import android.app.Application
import com.github.kittinunf.fuel.core.FuelManager

class OpenWeatherConfig : Application() {
    override fun onCreate() {
        super.onCreate()

        FuelManager.instance.basePath = "http://api.openweathermap.org/data/2.5/"
        FuelManager.instance.baseParams = listOf(
            "units" to "metric", // WeatherFields.main.temp をセルシウス温度で取得
            "lang" to "ja"
        )
    }
}