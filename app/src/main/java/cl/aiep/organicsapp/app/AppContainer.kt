package cl.aiep.organicsapp.app

import android.content.Context
import cl.aiep.organicsapp.data.auth.AuthRepository
import cl.aiep.organicsapp.data.auth.LocalAuthRepository
import cl.aiep.organicsapp.data.catalog.LocalProductRepository
import cl.aiep.organicsapp.data.catalog.ProductRepository
import cl.aiep.organicsapp.data.order.InMemoryOrderRepository
import cl.aiep.organicsapp.data.order.OrderRepository
import cl.aiep.organicsapp.data.session.SessionRepository
import cl.aiep.organicsapp.data.session.UserSessionRepository
import cl.aiep.organicsapp.data.session.userSessionDataStore
import cl.aiep.organicsapp.notification.OfferNotificationManager

class AppContainer(context: Context) {
    val productRepository: ProductRepository = LocalProductRepository()
    val authRepository: AuthRepository = LocalAuthRepository()
    val sessionRepository: SessionRepository = UserSessionRepository(context.userSessionDataStore)
    val orderRepository: OrderRepository = InMemoryOrderRepository()
    val offerNotificationManager = OfferNotificationManager(context)
}
