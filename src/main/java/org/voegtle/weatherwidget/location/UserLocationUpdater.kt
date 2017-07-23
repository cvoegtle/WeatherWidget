package org.voegtle.weatherwidget.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

import com.google.android.gms.location.LocationServices

class UserLocationUpdater(val context: Context) {
  private val locationOrderStore: LocationOrderStore = LocationOrderStore(context)
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

  fun updateLocation() {
    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
      location?.let {
        val userPosition = Position(latitude = it.latitude.toFloat(),
            longitude = it.longitude.toFloat())
        locationOrderStore.writePosition(userPosition)
      }
    }
  }


}