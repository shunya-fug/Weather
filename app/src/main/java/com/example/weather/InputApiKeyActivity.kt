package com.example.weather

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import kotlinx.android.synthetic.main.activity_input_api_key.*

class InputApiKeyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_api_key)

        apiKeySetButton.setOnClickListener { onApiKeySetButtonTapped(it) }

        // apiKey が共有プリファレンスに保存されているときは自動入力
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultText = "Input API Key."
        val apiKey = pref.getString("apiKey", defaultText)
        if (apiKey != defaultText) {
            apiKeyInput.setText(apiKey)
        }
    }

    /**
     * apiKeySetButton が押された時に MainActivity に遷移する。
     * このとき apiKeyInput に入力した内容を intent に追加する。
     * また saveApiKey メソッドで apiKey を共有プリファレンスに保存する。
     */
    private fun onApiKeySetButtonTapped(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        val apiKey = apiKeyInput.text.toString()
        intent.putExtra("apiKey", apiKey)
        saveApiKey(apiKey)
        startActivity(intent)
    }

    /**
     * [apiKey] を共有プリファレンスに保存する。
     */
    private fun saveApiKey(apiKey: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.putString("apiKey", apiKey).apply()
    }
}