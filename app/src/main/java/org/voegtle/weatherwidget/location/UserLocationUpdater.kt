package org.voegtle.weatherwidget.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class UserLocationUpdater(val context: Context) {
  private val locationOrderStore: LocationOrderStore = LocationOrderStore(context)
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

  fun updateLocation() {
    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      return
    }
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener { location ->
      location?.let {
        val userPosition = Position(latitude = it.latitude.toFloat(),
            longitude = it.longitude.toFloat())
        locationOrderStore.writePosition(userPosition)
      }
    }
  }
}