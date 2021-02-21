package com.example.weather

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_input_api_key.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking

class InputApiKeyActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private var lat: String = "35.4122" // 緯度（初期値は東京）
    private var lon: String = "139.4130" // 経度（初期値は東京）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_api_key)

        apiKeySetButton.setOnClickListener { onApiKeySetButtonTapped(it) }

        // apiKey が共有プリファレンスに保存されているときは自動入力
        loadApiKey()?.let { apiKeyInput.setText(it) }

        // GPS 取得
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000)
        } else {
            locationStart()

            if (::locationManager.isInitialized) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    50f,
                    this)
            }
        }
    }

    private fun locationStart() {
        Log.d("debug", "locationStart()")

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled")
        } else {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            Log.d("debug", "checkSelfPermission false")
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            50f,
            this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true")

                locationStart()
            } else {
                val toast = Toast.makeText(this,
                    "can't do anything.", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    /**
     * apiKeySetButton が押された時の挙動を定義。
     * API Key が正しければ [getWeatherData] メソッドで天気情報を取得し、`MainActivity` に遷移する。
     * 不正な API Key だった場合はアラートダイアログを表示する。
     */
    private fun onApiKeySetButtonTapped(view: View?) {
        val weatherFields = apiKeyInput.text.toString().let { apiKey ->
            saveApiKey(apiKey)
            getWeatherData(apiKey)
        }

        if (weatherFields != null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("weather", weatherFields.weather.first().description)
                putExtra("cityName", weatherFields.name)
                putExtra("lat", weatherFields.coord.lat.toString())
                putExtra("lon", weatherFields.coord.lon.toString())
                putExtra("temp", weatherFields.main.temp.toString() + "℃")
            }

            startActivity(intent)
        } else {
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle("API-Key is incorrect.")
                .setMessage("不正な API Key です。")
                .setPositiveButton("OK") { _, _ ->
                    // TODO:Yesが押された時の挙動
                }
                .show()
        }
    }

    /**
     * 指定した [apiKey] と GPS から取得した [lat], [lon] を利用して
     * OpenWeatherMap API にアクセスし、天気情報 [WeatherFields] を返却する。
     * [apiKey] が不正な場合は `null` を返却する。
     */
    private fun getWeatherData(apiKey: String): WeatherFields? {
        // moshi: JSONパーサー
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val params = listOf(
            "lat" to lat,
            "lon" to lon,
            "appid" to apiKey)

        // Fuel: HTTP通信ライブラリ
        return runBlocking {
            val (_, _, result) = Fuel.get("weather", params).awaitStringResponseResult()
            return@runBlocking result.fold(
                { data ->
                    return@fold moshi.adapter(WeatherFields::class.java).fromJson(data) },
                { error ->
                    error.printStackTrace()
                    return@fold null }
            )
        }
    }

    /**
     * [apiKey] を共有プリファレンスに保存する。
     */
    private fun saveApiKey(apiKey: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.putString("apiKey", apiKey).apply()
    }

    /**
     * API Key を共有プリファレンスから読み込む。
     * 読み込めなかったときは `null` を返す。
     */
    private fun loadApiKey(): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        return pref.getString("apiKey", null)
    }

    /**
     * GPS で現在地を取得し、[location] が `null` でなければ [lat] と [lon] を更新する。
     */
    override fun onLocationChanged(location: Location?) {
        location?.run {
            lat = latitude.toString()
            lon = longitude.toString()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }
}