package com.example.assignment.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat

class LocationTracker(
    private val context: Context
) {

    private val locationManager =
        context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

    private var listener: LocationListener? = null

    fun hasLocationPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun start(
        onLocationChanged: (Location) -> Unit
    ) {

        if (!hasLocationPermission()) return

        stop()

        val newListener =
            object : LocationListener {

                override fun onLocationChanged(
                    location: Location
                ) {
                    onLocationChanged(location)
                }
            }

        listener = newListener

        if (
            locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5_000L,
                100f,
                newListener,
                Looper.getMainLooper()
            )
        }

        if (
            locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5_000L,
                100f,
                newListener,
                Looper.getMainLooper()
            )
        }

        val lastGps =
            locationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            )

        val lastNetwork =
            locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER
            )

        val lastLocation =
            lastGps ?: lastNetwork

        if (lastLocation != null) {
            onLocationChanged(lastLocation)
        }
    }

    fun stop() {

        listener?.let {
            locationManager.removeUpdates(it)
        }

        listener = null
    }
}