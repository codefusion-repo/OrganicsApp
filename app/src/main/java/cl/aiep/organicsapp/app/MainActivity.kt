package cl.aiep.organicsapp.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.aiep.organicsapp.core.designsystem.OrganicsAppTheme
import cl.aiep.organicsapp.core.model.AppDestination
import cl.aiep.organicsapp.core.model.UserProfile
import cl.aiep.organicsapp.feature.auth.AuthSheet
import cl.aiep.organicsapp.feature.cart.CartSheet
import cl.aiep.organicsapp.feature.catalog.CatalogScreen
import cl.aiep.organicsapp.feature.orders.OrdersScreen
import cl.aiep.organicsapp.notification.OfferNotificationManager

class MainActivity : ComponentActivity() {

    private val container by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        container.offerNotificationManager.createChannel()

        setContent {
            OrganicsAppTheme {
                val appViewModel: AppViewModel = viewModel(
                    factory = AppViewModel.Factory(
                        productRepository = container.productRepository,
                        authRepository = container.authRepository,
                        sessionRepository = container.sessionRepository,
                        orderRepository = container.orderRepository
                    )
                )

                OrganicsApp(
                    viewModel = appViewModel,
                    notificationManager = container.offerNotificationManager
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrganicsApp(
    viewModel: AppViewModel,
    notificationManager: OfferNotificationManager
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onNotificationPermissionHandled(granted)
        if (granted) {
            notificationManager.showOffer()
        }
    }

    LaunchedEffect(state.notificationPermissionPending) {
        if (!state.notificationPermissionPending) return@LaunchedEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationManager.canPostNotifications()
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.onNotificationPermissionHandled(true)
            notificationManager.showOffer()
        }
    }

    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message.toUiText())
        viewModel.consumeMessage()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                user = state.user,
                cartCount = state.cartCount,
                onAuthClick = viewModel::openAuth,
                onCartClick = viewModel::openCart,
                onLogout = viewModel::logout
            )
        },
        bottomBar = {
            AppBottomBar(
                selected = state.destination,
                onSelected = viewModel::setDestination
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (state.destination) {
                AppDestination.CATALOG -> CatalogScreen(
                    products = state.filteredProducts,
                    selectedCategory = state.selectedCategory,
                    searchQuery = state.searchQuery,
                    receiveOffers = state.user?.receiveOffers == true,
                    isAuthenticated = state.user?.isGuest == false && state.user != null,
                    onSearchChange = viewModel::setSearchQuery,
                    onCategorySelected = viewModel::selectCategory,
                    onAddProduct = viewModel::addProduct,
                    onAuthRequested = viewModel::openAuth
                )

                AppDestination.ORDERS -> OrdersScreen(orders = state.orders)
            }
        }
    }

    if (state.showAuthSheet) {
        AuthSheet(
            error = state.authError,
            onDismiss = viewModel::closeAuth,
            onLogin = viewModel::login,
            onRegister = viewModel::register,
            onContinueAsGuest = viewModel::continueAsGuest
        )
    }

    if (state.showCartSheet) {
        CartSheet(
            items = state.cartItems,
            itemCount = state.cartCount,
            total = state.cartTotal,
            onDismiss = viewModel::closeCart,
            onIncrement = viewModel::incrementProduct,
            onDecrement = viewModel::decrementProduct,
            onConfirm = viewModel::confirmOrder
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    user: UserProfile?,
    cartCount: Int,
    onAuthClick: () -> Unit,
    onCartClick: () -> Unit,
    onLogout: () -> Unit
) {
    var accountMenuExpanded by remember { mutableStateOf(false) }
    val loggedInUser = user?.takeIf { !it.isGuest }

    TopAppBar(
        title = {
            Text(
                text = "OrganicsApp",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge { Text(cartCount.toString()) }
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Abrir carrito")
                }
            }

            if (loggedInUser == null) {
                TextButton(onClick = onAuthClick) {
                    Text("Ingresar")
                }
            } else {
                Box {
                    IconButton(onClick = { accountMenuExpanded = true }) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loggedInUser.name.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = accountMenuExpanded,
                        onDismissRequest = { accountMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = loggedInUser.name,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {},
                            enabled = false,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                accountMenuExpanded = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun AppBottomBar(
    selected: AppDestination,
    onSelected: (AppDestination) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = selected == AppDestination.CATALOG,
            onClick = { onSelected(AppDestination.CATALOG) },
            icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
            label = { Text("Productos") }
        )
        NavigationBarItem(
            selected = selected == AppDestination.ORDERS,
            onClick = { onSelected(AppDestination.ORDERS) },
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("Pedidos") }
        )
    }
}

private fun AppMessage.toUiText(): String = when (this) {
    AppMessage.PRODUCT_ADDED -> "Producto agregado al carrito."
    AppMessage.ORDER_CONFIRMED -> "Pedido confirmado correctamente."
    AppMessage.SESSION_STARTED -> "Sesión iniciada correctamente."
    AppMessage.SESSION_CLOSED -> "Sesión cerrada."
    AppMessage.NOTIFICATION_PERMISSION_DENIED -> "No se habilitaron las notificaciones de ofertas."
}
