package com.example.assignment.data

import com.example.assignment.R
import com.example.assignment.model.HomeFoodItem
import com.example.assignment.model.Reservation

//temporary only
//Dummy Data
val foodList = listOf(
    HomeFoodItem(
        R.drawable.cinnamon_roll,
        "Cinnamon Roll",
        "Soft cinnamon roll with sweet cinnamon filling and icing drizzle.",
        15.00,
        3,
        "6:30-7:30 PM",
        70
    ),
    HomeFoodItem(R.drawable.nasi_lemak,"Nasi Goreng Special", "Flavorful chicken fried rice with vegetables and egg.",18.00, 5, "7-9 PM",  45),
    HomeFoodItem(R.drawable.sushi,"Sushi Surprise Bag", "",12.00, 2, "5-7 PM",  25)
)
val restaurantSpecificFoods = listOf(
    HomeFoodItem(
        imageResId = R.drawable.cinnamon_roll,
        title = "Cinnamon Roll",
        description = "Soft cinnamon roll with sweet cinnamon filling and icing drizzle.",
        oriPrice = 15.00,
        quantity = 3,
        timeLabel = "6:30 - 7:30 PM",
        discountPercentage = 70
    ),
    HomeFoodItem(
        imageResId = R.drawable.croissant,
        title = "Croissant",
        description = "Flaky and buttery croissant, freshly baked and available at a discounted price.",
        oriPrice = 8.00,
        quantity = 2,
        timeLabel = "6:30 - 7:30 PM",
        discountPercentage = 50
    )
)
val reservationsList = listOf(
    Reservation(
        orderId = "RSV-20260718-001",
        imageResId = R.drawable.croissant,
        foodName = "Croissant Set",
        restaurantName = "Boulangerie Bakery",
        pickupTimeRange = "7:00 PM - 8:00 PM",
        pickupCountdown = "Pickup starts in 17h 59m",
        price = 5.90,
        quantity = 1,
        address = "12, Jalan Bukit Bintang, KL",
        distance = "1.2 km away",
        code = "A7X92K"
    )

)