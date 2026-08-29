package com.example.assignment.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.assignment.BuildConfig
import com.example.assignment.model.Restaurant
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val GOOGLE_MAPS_API_KEY =
    BuildConfig.GOOGLE_MAPS_API_KEY

fun googleMapThumbnailUrl(
    latitude: Double,
    longitude: Double
): String {

    return "https://maps.googleapis.com/maps/api/staticmap" +
            "?center=$latitude,$longitude" +
            "&zoom=16" +
            "&size=600x300" +
            "&scale=2" +
            "&maptype=roadmap" +
            "&markers=color:red%7C$latitude,$longitude" +
            "&key=$GOOGLE_MAPS_API_KEY"
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun googleMapThumbnailUrl(
    address: String
): String {

    val encodedAddress =
        URLEncoder.encode(
            address,
            StandardCharsets.UTF_8.toString()
        )

    return "https://maps.googleapis.com/maps/api/staticmap" +
            "?center=$encodedAddress" +
            "&zoom=16" +
            "&size=600x300" +
            "&scale=2" +
            "&maptype=roadmap" +
            "&markers=color:red%7C$encodedAddress" +
            "&key=$GOOGLE_MAPS_API_KEY"
}

fun openGoogleMaps(
    context: Context,
    restaurant: Restaurant
) {

    val uri = Uri.parse(
        "geo:${restaurant.latitude}," +
                "${restaurant.longitude}" +
                "?q=${restaurant.latitude}," +
                "${restaurant.longitude}" +
                "(${Uri.encode(restaurant.name)})"
    )

    val intent =
        Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply {

            setPackage(
                "com.google.android.apps.maps"
            )
        }

    try {

        context.startActivity(intent)

    } catch (_: Exception) {

        val fallback =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "https://www.google.com/maps/search/" +
                            "?api=1" +
                            "&query=" +
                            "${restaurant.latitude}," +
                            "${restaurant.longitude}"
                )
            )

        context.startActivity(fallback)
    }
}