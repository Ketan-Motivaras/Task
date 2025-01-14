package com.example.task2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class WidgetServiceReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_START_LOCATION_SERVICE = "START_LOCATION_SERVICE"
        const val ACTION_STOP_LOCATION_SERVICE = "STOP_LOCATION_SERVICE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WidgetServiceReceiver", "Action received: ${intent.action}")

        when (intent.action) {
            ACTION_START_LOCATION_SERVICE -> {
                val serviceIntent = Intent(context, LocationService::class.java).apply {
                    action = ACTION_START_LOCATION_SERVICE
                }
                context.startService(serviceIntent)
            }
            ACTION_STOP_LOCATION_SERVICE -> {
                val serviceIntent = Intent(context, LocationService::class.java).apply {
                    action = ACTION_STOP_LOCATION_SERVICE
                }
                context.startService(serviceIntent)
            }
            else -> {
                Log.w("WidgetServiceReceiver", "Unknown action received")
            }
        }
    }
}


