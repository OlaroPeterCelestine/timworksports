package com.timworksports.crestedpass.ui.shop

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.MerchItem
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.home.HomeViewModel
import com.timworksports.crestedpass.ui.media.imageRes
import com.timworksports.crestedpass.ui.theme.Gold

@Composable
fun ShopScreen(
    factory: CrestedPassViewModelFactory,
    vm: HomeViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    var open by remember { mutableStateOf<MerchItem?>(null) }
    var orders by remember { mutableStateOf(setOf<String>()) }
    val item = open
    BackHandler(enabled = item != null) { open = null }
    if (item != null) {
        MerchDetail(
            item = item,
            ordered = item.id in orders,
            onOrder = { orders = orders + item.id },
            onBack = { open = null }
        )
        return
    }
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        ScreenHeader(
            "Shop",
            if (orders.isEmpty()) "Order kit online" else if (orders.size == 1) "1 order placed" else "${orders.size} orders placed"
        )
        Spacer(Modifier.height(16.dp))
        state.merch.forEach { product ->
            BrandCard(onClick = { open = product }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(imageRes(product.imageName)),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        GoldLabel(product.category)
                        Text(product.name, color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Caption(product.sizes)
                        Spacer(Modifier.height(4.dp))
                        Text(product.price.asUgx(), color = colors.onBackground, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MerchDetail(
    item: MerchItem,
    ordered: Boolean,
    onOrder: () -> Unit,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val sizes = item.sizes.split(" · ").map { it.trim() }.filter { it.isNotEmpty() }
    var size by remember(item.id) { mutableStateOf(sizes.firstOrNull().orEmpty()) }
    val reference = "TW-${item.id.removePrefix("m").padStart(4, '0')}"
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenHeader(item.name, item.category, onBack = onBack)
        Image(
            painter = painterResource(imageRes(item.imageName)),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Text(item.price.asUgx(), color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
        Caption(item.detail)
        if (sizes.size > 1) {
            GoldLabel("Size")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sizes.forEach { option ->
                    val selected = option == size
                    Text(
                        option,
                        color = if (selected) Color.Black else colors.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) Gold else colors.surface)
                            .clickable { size = option }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        } else {
            Caption(item.sizes)
        }
        PrimaryButton(
            text = if (ordered) "Order placed" else "Order · ${item.price.asUgx()}",
            onClick = onOrder,
            gold = true,
            enabled = !ordered
        )
        if (ordered) {
            Caption("$reference · $size · Kampala delivery in 2–4 days. Pay on delivery in this prototype.")
        }
    }
}
