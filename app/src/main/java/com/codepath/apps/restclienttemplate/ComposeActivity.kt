package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    lateinit var tvWordCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        tvWordCount = findViewById(R.id.tvWordCount)
        val defaultColor = tvWordCount.currentTextColor
        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        etCompose.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val charAvailability = MAX_CHAR_COUNT - s.toString().length
                if (charAvailability < 0) {
                    tvWordCount.text = "0"
                    tvWordCount.setTextColor(Color.RED)
                    btnTweet.isEnabled = false
                } else {
                    tvWordCount.text = (MAX_CHAR_COUNT - s.toString().length).toString()
                    tvWordCount.setTextColor(defaultColor)
                    btnTweet.isEnabled = true
                }

            }
        })

        client = TwitterApplication.getRestClient(this)
        btnTweet.setOnClickListener {
            // grab the content of the edittext (etCompose)
            val tweetContent = etCompose.text.toString()
            // Tweet validation (not empty, under character count)
            // API call to publish tweet
            when {
                tweetContent.isEmpty() -> Toast.makeText(this, "If you have nothing to say, don't say anything", Toast.LENGTH_SHORT).show()
                tweetContent.length > MAX_CHAR_COUNT -> Toast.makeText(this, "Trim it down. This is not an essay!", Toast.LENGTH_SHORT).show()
                else -> client.postTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.i(TAG, "Status Code: $statusCode, throwable: $throwable")
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "OnSuccess: $statusCode")
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                })
            }
        }
    }

    companion object {
        val TAG = "ComposeActivity"
        val MAX_CHAR_COUNT = 280
    }
}