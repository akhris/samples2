package test

import domain.Worker
import java.util.UUID

object Workers {
    val worker1 = Worker(UUID.randomUUID().toString(),name = "Alex", surname = "Johnson", place = Rooms.Place1, email = "johnson@firm.com")
    val worker2 = Worker(UUID.randomUUID().toString(),name = "Nick", surname = "Armstrong", place = Rooms.Place2, email = "armstrong@firm.com")
    val worker3 = Worker(UUID.randomUUID().toString(),name = "Jack", surname = "Jackson", place = Rooms.Place3, email = "jackson@firm.com")
    val list = listOf(worker1, worker2, worker3)
}