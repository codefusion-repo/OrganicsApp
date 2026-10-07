package cl.aiep.organicsapp.core.util

import cl.aiep.organicsapp.core.model.CartItem

object CartCalculator {
    fun total(items: List<CartItem>): Int = items.sumOf { it.subtotal }
    fun itemCount(items: List<CartItem>): Int = items.sumOf { it.quantity }
}
