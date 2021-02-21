package com.example.weather

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weather.text = intent.getStringExtra("weather")
        cityName.text = intent.getStringExtra("cityName")
        temp.text = intent.getStringExtra("temp")
    }
}
