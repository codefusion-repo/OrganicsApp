package cl.aiep.organicsapp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.aiep.organicsapp.core.model.AppDestination
import cl.aiep.organicsapp.core.model.CartItem
import cl.aiep.organicsapp.core.model.OrderLine
import cl.aiep.organicsapp.core.model.Product
import cl.aiep.organicsapp.core.model.ProductCategory
import cl.aiep.organicsapp.core.model.UserProfile
import cl.aiep.organicsapp.data.auth.AuthRepository
import cl.aiep.organicsapp.data.auth.AuthResult
import cl.aiep.organicsapp.data.catalog.ProductRepository
import cl.aiep.organicsapp.data.order.OrderRepository
import cl.aiep.organicsapp.data.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val localState = MutableStateFlow(
        AppUiState(products = productRepository.getProducts())
    )

    val uiState: StateFlow<AppUiState> = combine(
        localState,
        sessionRepository.profile,
        orderRepository.orders
    ) { state, profile, orders ->
        state.copy(user = profile, orders = orders)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = localState.value
    )

    fun setDestination(destination: AppDestination) {
        localState.update { it.copy(destination = destination) }
    }

    fun setSearchQuery(query: String) {
        localState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: ProductCategory) {
        localState.update { it.copy(selectedCategory = category) }
    }

    fun openAuth() {
        localState.update { it.copy(showAuthSheet = true, authError = null) }
    }

    fun closeAuth() {
        localState.update { it.copy(showAuthSheet = false, authError = null) }
    }

    fun openCart() {
        localState.update { it.copy(showCartSheet = true) }
    }

    fun closeCart() {
        localState.update { it.copy(showCartSheet = false) }
    }

    fun login(email: String, password: String, receiveOffers: Boolean) {
        viewModelScope.launch {
            when (val result = authRepository.login(email, password, receiveOffers)) {
                is AuthResult.Success -> startSession(result.profile)
                is AuthResult.Error -> localState.update { it.copy(authError = result.message) }
            }
        }
    }

    fun register(name: String, email: String, password: String, receiveOffers: Boolean) {
        viewModelScope.launch {
            when (val result = authRepository.register(name, email, password, receiveOffers)) {
                is AuthResult.Success -> startSession(result.profile)
                is AuthResult.Error -> localState.update { it.copy(authError = result.message) }
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            startSession(
                UserProfile(
                    name = "Invitado",
                    email = "",
                    receiveOffers = false,
                    isGuest = true
                )
            )
        }
    }

    private suspend fun startSession(profile: UserProfile) {
        sessionRepository.save(profile)
        localState.update {
            it.copy(
                showAuthSheet = false,
                authError = null,
                notificationPermissionPending = profile.receiveOffers,
                message = AppMessage.SESSION_STARTED
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.clear()
            localState.update {
                it.copy(
                    destination = AppDestination.CATALOG,
                    message = AppMessage.SESSION_CLOSED
                )
            }
        }
    }

    fun addProduct(product: Product) {
        localState.update { state ->
            val existing = state.cartItems.firstOrNull { it.product.id == product.id }
            val newItems = if (existing == null) {
                state.cartItems + CartItem(product, 1)
            } else {
                state.cartItems.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            }
            state.copy(cartItems = newItems, message = AppMessage.PRODUCT_ADDED)
        }
    }

    fun incrementProduct(productId: String) {
        localState.update { state ->
            state.copy(
                cartItems = state.cartItems.map {
                    if (it.product.id == productId) it.copy(quantity = it.quantity + 1) else it
                }
            )
        }
    }

    fun decrementProduct(productId: String) {
        localState.update { state ->
            state.copy(
                cartItems = state.cartItems.mapNotNull {
                    if (it.product.id != productId) it
                    else if (it.quantity <= 1) null
                    else it.copy(quantity = it.quantity - 1)
                }
            )
        }
    }

    fun confirmOrder() {
        val state = localState.value
        if (state.cartItems.isEmpty()) return

        val user = uiState.value.user
        val customer = when {
            user == null -> "Invitado"
            user.isGuest -> "Invitado"
            else -> user.name
        }

        orderRepository.createOrder(
            customerLabel = customer,
            lines = state.cartItems.map {
                OrderLine(
                    productName = it.product.name,
                    quantity = it.quantity,
                    subtotal = it.subtotal
                )
            },
            total = state.cartTotal
        )

        localState.update {
            it.copy(
                cartItems = emptyList(),
                showCartSheet = false,
                destination = AppDestination.ORDERS,
                message = AppMessage.ORDER_CONFIRMED
            )
        }
    }

    fun consumeMessage() {
        localState.update { it.copy(message = null) }
    }

    fun onNotificationPermissionHandled(granted: Boolean) {
        localState.update {
            it.copy(
                notificationPermissionPending = false,
                message = if (granted) it.message else AppMessage.NOTIFICATION_PERMISSION_DENIED
            )
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val authRepository: AuthRepository,
        private val sessionRepository: SessionRepository,
        private val orderRepository: OrderRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(
                productRepository = productRepository,
                authRepository = authRepository,
                sessionRepository = sessionRepository,
                orderRepository = orderRepository
            ) as T
        }
    }
}
