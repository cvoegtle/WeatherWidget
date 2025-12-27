package org.voegtle.wetterwolkewatch.io

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class AppMessenger(private val context: Context) {
    private val TAG = this::class.java.simpleName

    suspend fun requestDataUpdate() {
        requestAsync()
    }

    private suspend fun requestAsync() {
        try {
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()
            nodes.forEach { node ->
                Wearable.getMessageClient(context).sendMessage(
                    node.id,
                    "/refresh-data",
                    ByteArray(0)
                ).apply {
                    addOnSuccessListener { Log.d(TAG, "Request sent to ${node.displayName}") }
                    addOnFailureListener { Log.e(TAG, "Failed to send request to ${node.displayName}", it) }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request data update", e)
        }
    }

}
