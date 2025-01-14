package com.example.task2

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null
    private var isServiceRunning = false
    private val notificationChannelId = "location_service_channel"

    private lateinit var csvFile: File

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createCsvFile()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        Log.d("LocationService", "LocationService started with action: $action")
        when (action) {
            "START_LOCATION_SERVICE" -> if (!isServiceRunning) startLocationUpdates()
            "STOP_LOCATION_SERVICE" -> if (isServiceRunning) stopLocationUpdates()
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                updateLocationNotification(location)
                saveLocationToCsv(location) // Save location to CSV
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationService", "Location permissions not granted!")
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000L, // Update interval in milliseconds (2 seconds)
            10f,  // Minimum distance in meters before update
            locationListener!!
        )

        isServiceRunning = true
        val notification = createNotification("Fetching location...")
        startForeground(1, notification)
    }

    private fun stopLocationUpdates() {
        locationListener?.let {
            locationManager.removeUpdates(it)
        }
        isServiceRunning = false
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Live Location")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun updateLocationNotification(location: Location) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notification = createNotification("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
        notificationManager.notify(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            notificationChannelId,
            "Location Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createCsvFile() {
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }
        val fileName = "Location_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        csvFile = File(directory, fileName)

        try {
            FileWriter(csvFile, true).use { writer ->
                writer.append("Timestamp, Latitude, Longitude\n")
            }
            Log.d("LocationService", "CSV file created: ${csvFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("LocationService", "Error creating CSV file: ${e.message}")
        }
    }

    private fun saveLocationToCsv(location: Location) {
        try {
            FileWriter(csvFile, true).use { writer ->
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                writer.append("$timestamp, ${location.latitude}, ${location.longitude}\n")
            }
        } catch (e: IOException) {
            Log.e("LocationService", "Error writing to CSV file: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
