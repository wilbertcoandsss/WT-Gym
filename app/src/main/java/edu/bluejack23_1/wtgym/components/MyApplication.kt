package edu.bluejack23_1.wtgym.components

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val config = mapOf(
            "cloud_name" to "dw7bewmoo",
            "api_key" to "833636687498374",
            "api_secret" to "M9Md7785NcwAhdacBVkijLGxDVY"
        )

        // Initialize MediaManager here
        MediaManager.init(this, config)
    }
}