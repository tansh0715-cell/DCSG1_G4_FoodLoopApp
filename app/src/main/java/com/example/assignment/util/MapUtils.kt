package com.example.assignment.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.assignment.R
import com.example.assignment.model.Restaurant

fun mapboxStaticMapUrl(
    context: Context,
    longitude: Double,
    latitude: Double,
    zoom: Int = 15,
    width: Int = 800,
    height: Int = 400
): String {

    val accessToken =
        context.getString(
            R.string.mapbox_access_token
        )

    return "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/" +
            "pin-s+e74c3c($longitude,$latitude)/" +
            "$longitude,$latitude,$zoom/" +
            "${width}x$height" +
            "?access_token=$accessToken"
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