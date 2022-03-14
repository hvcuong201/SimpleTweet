package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            populateHomeTimeLine()
        }
        // Configure the refreshing colors

        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)

        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        scrollListener = object : EndlessRecyclerViewScrollListener(rvTweets.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "On Load More!")
                loadNextDataFromApi(tweets.size - 1)
            }
        }

        rvTweets.addOnScrollListener(scrollListener)
        populateHomeTimeLine()
    }

    private fun populateHomeTimeLine() {
        client.getHomeTimeline(object : JsonHttpResponseHandler() {

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure! $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess!")
                val jsonArray = json.jsonArray
                try {
                    Log.i(TAG, "$json")
                    adapter.clear()
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    adapter.addAll(listOfNewTweetsRetrieved)
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }

            }

        })
    }

    fun loadNextDataFromApi(offset: Int) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        val maxIdTweet = tweets[offset].id + 1
        client.getOlderTimeline(maxIdTweet, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure! $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess!")
                val jsonArray = json.jsonArray
                try {
                    Log.i(TAG, "LoadMore: $json")
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    adapter.addAll(listOfNewTweetsRetrieved)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }
            }

        })

    }

    companion object {
        val TAG = "TimelineActivity"
    }
}