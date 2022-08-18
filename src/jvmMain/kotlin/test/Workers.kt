package test

import domain.Worker
import java.util.UUID

object Workers {
    val worker1 = Worker(UUID.randomUUID().toString(),name = "Alex", surname = "Johnson", room = Rooms.Room1, email = "johnson@firm.com")
    val worker2 = Worker(UUID.randomUUID().toString(),name = "Nick", surname = "Armstrong", room = Rooms.Room2, email = "armstrong@firm.com")
    val worker3 = Worker(UUID.randomUUID().toString(),name = "Jack", surname = "Jackson", room = Rooms.Room3, email = "jackson@firm.com")
    val list = listOf(worker1, worker2, worker3)
}