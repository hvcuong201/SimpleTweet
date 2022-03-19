package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class User(var name: String = "", var screenName: String = "", var publicImageUrl: String = ""): Parcelable {

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