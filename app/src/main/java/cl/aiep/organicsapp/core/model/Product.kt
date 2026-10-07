package cl.aiep.organicsapp.core.model

data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val price: Int,
    val description: String,
    val unit: String,
    val shortLabel: String,
    val featured: Boolean = false
)
