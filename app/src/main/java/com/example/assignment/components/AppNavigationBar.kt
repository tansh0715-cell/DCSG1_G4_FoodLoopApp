package com.example.assignment.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.ui.theme.navigationItemColors

@Composable
fun AppNavigationBar(navController: NavController, isConsumer: Boolean ){
    var selectedItem by remember { mutableStateOf(if(isConsumer) "home" else "order") }

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(16.dp, spotColor = MaterialTheme.colorScheme.onSecondary, ambientColor = MaterialTheme.colorScheme.onSecondary)
    ) {
        NavigationBarItem(
            selected = selectedItem == "home",
            onClick = {
                selectedItem = "home"
                navController.navigate("home"){
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.home_4_svgrepo_com),
                    contentDescription = "Home",
                    tint =  if(selectedItem == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Home",style = MaterialTheme.typography.labelMedium)},
            colors = navigationItemColors()
        )

        //Order button
        NavigationBarItem(
            selected = selectedItem == "order",
            onClick = {
                selectedItem = "order"
                navController.navigate("order"){
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.order_svgrepo_com),
                    contentDescription = "order",
                    tint =  if(selectedItem == "order") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text(if(isConsumer) "My Order" else "Order",style = MaterialTheme.typography.labelMedium)},
            colors = navigationItemColors()
        )

        //Add button --> only for provider
        if(!isConsumer){
            NavigationBarItem(
                selected = selectedItem == "add",
                onClick = {
                    selectedItem = "add"
                    navController.navigate("add"){
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.add_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "add",
                        tint = if(selectedItem == "add") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text("Add",style = MaterialTheme.typography.labelMedium)},
                colors = navigationItemColors()
            )
        } else {
            //Fridge button
            NavigationBarItem(
                selected = selectedItem == "fridge",
                onClick = {
                    selectedItem = "fridge"
                    navController.navigate("inventory"){
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(R.drawable.fridge_svgrepo_com),
                        contentDescription = "fridge",
                        tint =  if(selectedItem == "fridge") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text("Fridge",style = MaterialTheme.typography.labelMedium)},
                colors = navigationItemColors()
            )
        }

        //Profile button
        NavigationBarItem(
            selected = selectedItem == "profile",
            onClick = {
                selectedItem = "profile"
                navController.navigate("profile"){
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.account_circle_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = "profile",
                    tint =  if(selectedItem == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Profile",style = MaterialTheme.typography.labelMedium)},
            colors = navigationItemColors()
        )
    }
}