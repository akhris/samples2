package test

import domain.Room
import java.util.UUID

object Rooms {
    val Room1 = Room(roomID = UUID.randomUUID().toString(), roomNumber = "405", description = "Этаж 4")
    val Room2 = Room(roomID = UUID.randomUUID().toString(), roomNumber = "208", description = "Этаж 2")
    val Room3 = Room(roomID = UUID.randomUUID().toString(), roomNumber = "506", description = "Этаж 5")
    val list = listOf(Room1, Room2, Room3)
}