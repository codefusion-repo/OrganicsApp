package cl.aiep.organicsapp.app

import cl.aiep.organicsapp.core.model.AppDestination
import cl.aiep.organicsapp.core.model.CartItem
import cl.aiep.organicsapp.core.model.Order
import cl.aiep.organicsapp.core.model.Product
import cl.aiep.organicsapp.core.model.ProductCategory
import cl.aiep.organicsapp.core.model.UserProfile
import cl.aiep.organicsapp.core.util.CartCalculator

data class AppUiState(
    val user: UserProfile? = null,
    val destination: AppDestination = AppDestination.CATALOG,
    val products: List<Product> = emptyList(),
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val searchQuery: String = "",
    val cartItems: List<CartItem> = emptyList(),
    val orders: List<Order> = emptyList(),
    val showAuthSheet: Boolean = false,
    val showCartSheet: Boolean = false,
    val authError: String? = null,
    val notificationPermissionPending: Boolean = false,
    val message: AppMessage? = null
) {
    val filteredProducts: List<Product>
        get() = products.filter { product ->
            val categoryMatches = selectedCategory == ProductCategory.ALL || product.category == selectedCategory
            val query = searchQuery.trim()
            val searchMatches = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            categoryMatches && searchMatches
        }

    val cartCount: Int get() = CartCalculator.itemCount(cartItems)
    val cartTotal: Int get() = CartCalculator.total(cartItems)
}

enum class AppMessage {
    PRODUCT_ADDED,
    ORDER_CONFIRMED,
    SESSION_STARTED,
    SESSION_CLOSED,
    NOTIFICATION_PERMISSION_DENIED
}
