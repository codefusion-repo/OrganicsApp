package cl.aiep.organicsapp.data.order

import cl.aiep.organicsapp.core.model.Order
import cl.aiep.organicsapp.core.model.OrderLine
import kotlinx.coroutines.flow.StateFlow

interface OrderRepository {
    val orders: StateFlow<List<Order>>
    fun createOrder(customerLabel: String, lines: List<OrderLine>, total: Int): Order
}
