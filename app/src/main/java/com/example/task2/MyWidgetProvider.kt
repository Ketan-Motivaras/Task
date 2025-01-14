package com.example.task2

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Toast.makeText(context, "Widget Updated", Toast.LENGTH_SHORT).show()
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Set the click listener for the "Click Image" button
            val cameraIntent = Intent(context, CameraActivity::class.java)
            val cameraPendingIntent = PendingIntent.getActivity(
                context,
                0,
                cameraIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.btnClickImage, cameraPendingIntent)

            // Set listener for the "Start Location Service" button
            val startServiceIntent = Intent(context, WidgetServiceReceiver::class.java).apply {
                action = WidgetServiceReceiver.ACTION_START_LOCATION_SERVICE
            }
            val startServicePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                startServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.btnStartLocation, startServicePendingIntent)

            // Set listener for the "Stop Location Service" button
            val stopServiceIntent = Intent(context, WidgetServiceReceiver::class.java).apply {
                action = WidgetServiceReceiver.ACTION_STOP_LOCATION_SERVICE
            }
            val stopServicePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.btnStopLocation, stopServicePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}


