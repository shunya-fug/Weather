package com.example.weather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityName.text = intent.getStringExtra("cityName")
        latitude.text = "緯度：" + intent.getStringExtra("lat")
        longitude.text = "経度：" + intent.getStringExtra("lon")
        weather.text = intent.getStringExtra("weather")
        temp.text = intent.getStringExtra("temp")
    }
}
