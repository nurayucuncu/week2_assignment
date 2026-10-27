package com.example.week2

import androidx.compose.runtime.Immutable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable


@Immutable
data class HardwareItem(
    val id: String,
    val name: String,
    val category: String,
    val cost: Double,
    val stockCount: Int
)
val hardwareItems = listOf(
    HardwareItem("101", "MacBook Pro M3 Max", "Geliştirici Bilgisayarı", 3499.0, 5),
    HardwareItem("102", "NVIDIA RTX 4090 Sunucu", "Yapay Zeka / GPU", 1599.0, 2),
    HardwareItem("103", "Google Pixel 8 Pro", "Mobil Test Cihazı", 999.0, 8),
    HardwareItem("104", "Dell UltraSharp 49\"", "Monitör", 1199.0, 4),
    HardwareItem("105", "Sony WH-1000XM5 Kulaklık", "Odaklanma Ekipmanı", 399.0, 12),
    HardwareItem("106", "Raspberry Pi 5 8GB", "IoT Geliştirme Kiti", 85.0, 25),
    HardwareItem("107", "HHKB Professional Keyboard", "Ergonomik Klavye", 299.0, 0),
    HardwareItem("108", "AMD Ryzen Threadripper", "Yapay Zeka / GPU", 4999.0, 1)
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DevClusterApp()
                }
            }
        }
    }
}

@Composable
fun DevClusterApp() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var totalCost by remember { mutableStateOf(0.0) }

    var stockById by remember {
        mutableStateOf(hardwareItems.associate { it.id to it.stockCount })
    }

    val filteredItems = remember(searchQuery) {
        hardwareItems.filter { item ->
            item.name.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "DevCluster",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        HardwareSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Total Cost: $${"%.2f".format(totalCost)}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = filteredItems,
                key = { item -> item.id }
            ) { item ->
                val currentStock = stockById[item.id] ?: item.stockCount

                HardwareCard(
                    item = item,
                    currentStock = currentStock,
                    onBuy = {
                        if (currentStock > 0) {
                            stockById = stockById.toMutableMap().apply {
                                this[item.id] = currentStock - 1
                            }
                            totalCost += item.cost
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun HardwareSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Donanım veya kategori ara") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun HardwareCard(
    item: HardwareItem,
    currentStock: Int,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBuy)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Kategori: ${item.category}")
            Text(text = "Fiyat: $${"%.2f".format(item.cost)}")
            Text(text = "Stok: $currentStock")
            Text(
                text = if (currentStock > 0) "Satın almak için karta dokun" else "Stokta yok",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}