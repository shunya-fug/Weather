package com.example.weather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.run {
            cityName.text = getStringExtra("cityName")
            latitude.text = "緯度：" + getStringExtra("lat")
            longitude.text = "経度：" + getStringExtra("lon")
            weather.text = getStringExtra("weather")
            temp.text = getStringExtra("temp")
        }
    }
}
