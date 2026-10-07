package cl.aiep.organicsapp.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.aiep.organicsapp.core.model.Product
import cl.aiep.organicsapp.core.model.ProductCategory
import cl.aiep.organicsapp.feature.catalog.components.CatalogHeader
import cl.aiep.organicsapp.feature.catalog.components.EmptyCatalogState
import cl.aiep.organicsapp.feature.catalog.components.OfferBanner
import cl.aiep.organicsapp.feature.catalog.components.ProductCard

@Composable
fun CatalogScreen(
    products: List<Product>,
    selectedCategory: ProductCategory,
    searchQuery: String,
    receiveOffers: Boolean,
    isAuthenticated: Boolean,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (ProductCategory) -> Unit,
    onAddProduct: (Product) -> Unit,
    onAuthRequested: () -> Unit
) {
    val categories = listOf(
        ProductCategory.ALL to "Todos",
        ProductCategory.FRESH to "Frescos",
        ProductCategory.PANTRY to "Despensa",
        ProductCategory.DRINKS to "Bebidas"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            CatalogHeader()
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Buscar productos") },
                placeholder = { Text("Ej. miel, avena, kombucha") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.large
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(categories) { (category, label) ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { onCategorySelected(category) },
                        label = { Text(label) }
                    )
                }
            }
        }

        item {
            OfferBanner(
                notificationsEnabled = receiveOffers,
                isAuthenticated = isAuthenticated,
                onAuthRequested = onAuthRequested
            )
        }

        item {
            Text(
                text = "Productos disponibles",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "${products.size} resultados para tu selección",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (products.isEmpty()) {
            item { EmptyCatalogState() }
        } else {
            items(
                items = products,
                key = { it.id }
            ) { product ->
                ProductCard(
                    product = product,
                    categoryLabel = categoryLabel(product.category),
                    onAdd = { onAddProduct(product) }
                )
            }
        }
    }
}

private fun categoryLabel(category: ProductCategory): String = when (category) {
    ProductCategory.ALL -> "Todos"
    ProductCategory.FRESH -> "Frescos"
    ProductCategory.PANTRY -> "Despensa"
    ProductCategory.DRINKS -> "Bebidas"
}
