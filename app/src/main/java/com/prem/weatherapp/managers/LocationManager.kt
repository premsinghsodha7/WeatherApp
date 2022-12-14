package com.prem.weatherapp.managers

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.prem.weatherapp.database.cities.Coord
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "Location Manager"

@SuppressLint("MissingPermission")
class LocationManager @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
) {

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.d(TAG, "onLocationResult: $locationResult")
            }

            override fun onLocationAvailability(la: LocationAvailability) {
                Log.d(TAG, "location available: ${la.isLocationAvailable}")
            }
        }
    }

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            interval = 600000
            fastestInterval = 300000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    init {
        hasPermission()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.d(TAG, "Starting")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        Log.d(TAG, "Stopping")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    fun hasPermission(): Boolean {
        val permGranted = ContextCompat.checkSelfPermission(
            fusedLocationClient.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (permGranted) startLocationUpdates() else stopLocationUpdates()
        return permGranted
    }

    suspend fun getLocation(): Coord? {
        return fusedLocationClient.lastLocation.await()?.run {
            Coord(latitude, longitude)
        }
    }
}