package cl.aiep.organicsapp.core.util

import cl.aiep.organicsapp.core.model.CartItem
import cl.aiep.organicsapp.core.model.Product
import cl.aiep.organicsapp.core.model.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class CartCalculatorTest {
    private val product = Product(
        id = "test",
        name = "Producto",
        category = ProductCategory.PANTRY,
        price = 2500,
        description = "Producto de prueba",
        unit = "Unidad",
        shortLabel = "PR"
    )

    @Test
    fun total_multipliesPriceByQuantity() {
        val items = listOf(CartItem(product, quantity = 3))
        assertEquals(7500, CartCalculator.total(items))
    }

    @Test
    fun itemCount_sumsQuantities() {
        val items = listOf(CartItem(product, 2), CartItem(product.copy(id = "other"), 1))
        assertEquals(3, CartCalculator.itemCount(items))
    }
}
