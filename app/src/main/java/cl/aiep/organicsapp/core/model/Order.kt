package cl.aiep.organicsapp.core.model

data class OrderLine(
    val productName: String,
    val quantity: Int,
    val subtotal: Int
)

data class Order(
    val id: String,
    val createdAtLabel: String,
    val customerLabel: String,
    val lines: List<OrderLine>,
    val total: Int,
    val status: OrderStatus = OrderStatus.CONFIRMED
) {
    val itemCount: Int get() = lines.sumOf { it.quantity }
}

enum class OrderStatus {
    CONFIRMED,
    PREPARING,
    DELIVERED
}
