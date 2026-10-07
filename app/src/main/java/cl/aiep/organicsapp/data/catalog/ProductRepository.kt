package cl.aiep.organicsapp.data.catalog

import cl.aiep.organicsapp.core.model.Product

interface ProductRepository {
    fun getProducts(): List<Product>
}
