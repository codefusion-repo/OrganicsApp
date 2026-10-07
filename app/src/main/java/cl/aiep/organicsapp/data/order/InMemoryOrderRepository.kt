package cl.aiep.organicsapp.data.order

import cl.aiep.organicsapp.core.model.Order
import cl.aiep.organicsapp.core.model.OrderLine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryOrderRepository : OrderRepository {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    override fun createOrder(
        customerLabel: String,
        lines: List<OrderLine>,
        total: Int
    ): Order {
        val dateFormatter = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.forLanguageTag("es-CL"))
        val order = Order(
            id = UUID.randomUUID().toString().take(8).uppercase(Locale.ROOT),
            createdAtLabel = dateFormatter.format(Date()),
            customerLabel = customerLabel,
            lines = lines,
            total = total
        )
        _orders.value = listOf(order) + _orders.value
        return order
    }
}
