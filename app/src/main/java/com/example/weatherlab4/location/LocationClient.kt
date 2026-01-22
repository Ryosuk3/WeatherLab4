package com.example.weatherlab4.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

class LocationClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fused: FusedLocationProviderClient
) {
    data class Options(val highAccuracy: Boolean, val timeoutSec: Int)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(opt: Options): Location? =
        suspendCancellableCoroutine { cont ->
            val prio = if (opt.highAccuracy) Priority.PRIORITY_HIGH_ACCURACY
            else Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val token = CancellationTokenSource()
            val timeout = kotlinx.coroutines.GlobalScope
            val req = CurrentLocationRequest.Builder()
                .setPriority(prio)
                .setMaxUpdateAgeMillis(5_000)
                .build()
            fused.getCurrentLocation(req, token.token)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
            cont.invokeOnCancellation { token.cancel() }
            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                kotlinx.coroutines.delay(opt.timeoutSec.seconds)
                if (cont.isActive) { token.cancel(); cont.resume(null) }
            }
        }
}