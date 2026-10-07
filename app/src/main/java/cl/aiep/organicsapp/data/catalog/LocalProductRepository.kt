package cl.aiep.organicsapp.data.catalog

import cl.aiep.organicsapp.core.model.Product
import cl.aiep.organicsapp.core.model.ProductCategory

class LocalProductRepository : ProductRepository {
    override fun getProducts(): List<Product> = listOf(
        Product(
            id = "apple",
            name = "Manzana orgánica",
            category = ProductCategory.FRESH,
            price = 2990,
            description = "Manzana roja de cultivo orgánico, fresca y crujiente.",
            unit = "Bolsa 1 kg",
            shortLabel = "MA",
            featured = true
        ),
        Product(
            id = "avocado",
            name = "Palta Hass",
            category = ProductCategory.FRESH,
            price = 4690,
            description = "Palta Hass seleccionada, ideal para consumo diario.",
            unit = "Bolsa 1 kg",
            shortLabel = "PH"
        ),
        Product(
            id = "lettuce",
            name = "Lechuga hidropónica",
            category = ProductCategory.FRESH,
            price = 1490,
            description = "Lechuga fresca cultivada sin suelo y lista para preparar.",
            unit = "Unidad",
            shortLabel = "LH"
        ),
        Product(
            id = "honey",
            name = "Miel natural",
            category = ProductCategory.PANTRY,
            price = 5490,
            description = "Miel multifloral de productor local, sin aditivos.",
            unit = "Frasco 500 g",
            shortLabel = "MN",
            featured = true
        ),
        Product(
            id = "oats",
            name = "Avena integral",
            category = ProductCategory.PANTRY,
            price = 3290,
            description = "Avena integral en hojuelas, fuente natural de fibra.",
            unit = "Bolsa 750 g",
            shortLabel = "AI"
        ),
        Product(
            id = "jam",
            name = "Mermelada artesanal",
            category = ProductCategory.PANTRY,
            price = 3990,
            description = "Mermelada de berries elaborada en pequeños lotes.",
            unit = "Frasco 300 g",
            shortLabel = "MB"
        ),
        Product(
            id = "kombucha",
            name = "Kombucha cítrica",
            category = ProductCategory.DRINKS,
            price = 2790,
            description = "Bebida fermentada con notas cítricas y bajo contenido de azúcar.",
            unit = "Botella 330 ml",
            shortLabel = "KC"
        ),
        Product(
            id = "juice",
            name = "Jugo prensado",
            category = ProductCategory.DRINKS,
            price = 3490,
            description = "Jugo prensado en frío de manzana, apio y limón.",
            unit = "Botella 500 ml",
            shortLabel = "JP"
        )
    )
}
