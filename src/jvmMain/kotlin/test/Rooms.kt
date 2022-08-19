package test

import domain.Place
import java.util.UUID

object Rooms {
    val Place1 = Place(placeID = UUID.randomUUID().toString(), roomNumber = "405", description = "Этаж 4")
    val Place2 = Place(placeID = UUID.randomUUID().toString(), roomNumber = "208", description = "Этаж 2")
    val Place3 = Place(placeID = UUID.randomUUID().toString(), roomNumber = "506", description = "Этаж 5")
    val list = listOf(Place1, Place2, Place3)
}