package com.example.assignment.screen.inventoryModule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.model.inventoryModule.Food
import com.example.assignment.ui.theme.appButtonColors
import androidx.compose.ui.text.font.FontWeight
import com.example.assignment.ui.theme.filterColors
import com.example.assignment.viewmodel.inventory.InventoryViewModel
import androidx.compose.ui.platform.LocalContext
@Composable
fun InventoryScreen(innerPadding: PaddingValues, navController: NavController, vm: InventoryViewModel) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.loadInventory(context)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = vm.selectedFilter == 0,
                onClick = { vm.selectFilter(0) },
                label = { Text("All") },
                shape = RoundedCornerShape(50.dp),
                colors = filterColors()
            );
            FilterChip(
                selected = vm.selectedFilter == 1,
                onClick = { vm.selectFilter(1) },
                label = { Text("Safe") },
                shape = RoundedCornerShape(50.dp),
                colors = filterColors()
            );
            FilterChip(
                selected = vm.selectedFilter == 2,
                onClick = { vm.selectFilter(2) },
                label = { Text("Expiring soon") },
                shape = RoundedCornerShape(50.dp),
                colors = filterColors()
            );
            FilterChip(
                selected = vm.selectedFilter == 3,
                onClick = { vm.selectFilter(3) },
                label = { Text("Expired") },
                shape = RoundedCornerShape(50.dp),
                colors = filterColors()
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(vm.filteredFoods) { food -> ElevatedCard(modifier = Modifier.fillMaxSize()
                .heightIn(min = 120.dp, max = 160.dp)
                .padding(horizontal = 25.dp, vertical = 10.dp)
                .clickable(){
                //clickable function here
                navController.navigate("ITEM_DETAIL/${food.item_id}")

            }) {
                Row() {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = food.name,
                            modifier = Modifier.offset(x = 12.dp, y = 18.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = food.reminder_days.toString() + " days until reminder",
                            modifier = Modifier.offset(x = 12.dp, y = 20.dp),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "Expire on " + food.expireDate,
                            modifier = Modifier.offset(x = 12.dp, y = 22.dp),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                    };
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) { StatusBadge(food.status)}
                }
            }
            }
        }

        ElevatedButton(onClick = { navController.navigate("ADD_INVENTORY") },
            shape = RoundedCornerShape(size = 20.dp),
            colors =appButtonColors())
        { Icon(painter = painterResource(R.drawable.add_24dp_e3e3e3_fill0_wght200_grad0_opsz24), contentDescription = "add");
            Text(text = "Add Item", style = MaterialTheme.typography.labelLarge) }
    }

}
