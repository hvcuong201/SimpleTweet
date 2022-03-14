package com.codepath.apps.restclienttemplate.models

import android.util.Log
import org.json.JSONObject

class User {

    var name: String = ""
    var screenName: String = ""
    var publicImageUrl: String = ""

    companion object {
        fun fromJson(jsonObject: JSONObject): User {
            val user = User()
            user.name = jsonObject.getString("name")
            user.screenName = jsonObject.getString("screen_name")

            var profileImageUrlString = jsonObject.getString("profile_image_url_https")
            profileImageUrlString = profileImageUrlString.slice(0..profileImageUrlString.length-11) + "bigger.jpg"
            user.publicImageUrl = profileImageUrlString

            return user
        }
    }
}