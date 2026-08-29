package com.example.assignment.screen.profileModule

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.R
import com.example.assignment.data.UserPreferencesManager
import com.example.assignment.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    authRepository: AuthRepository,
    userPreferencesManager: UserPreferencesManager
    ){
    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.fillMaxSize().padding(innerPadding)){
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background), modifier = Modifier.fillMaxWidth().height(170.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Image(painter = painterResource(R.drawable.ic_launcher_background), contentDescription = "ProfilePicture", contentScale = ContentScale.Crop, modifier = Modifier.clip(CircleShape).size(80.dp))
                    Text(text = "Abang Lee", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(5.dp))
                    Text(text = "@Lee_Bakery",style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondary)
                } } }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            ElevatedCard(modifier = Modifier.size(width = 100.dp, height = 80.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
                ) {
                    Text(
                        text = "12",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Meals Saved",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            ElevatedCard(modifier = Modifier.size(width = 100.dp, height = 80.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
                ) {

                    Text(
                        text = "RM45",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Money Saved",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            ElevatedCard(modifier = Modifier.size(width = 100.dp, height = 80.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
                ) {
                    Text(
                        text = "15",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Reservation",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            ElevatedCard(
                modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
            ) {
                ListItem(
                    headlineContent = { Text(text = "Edit profile") },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24),
                            contentDescription = "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.clickable() {
                        //button function here
                    })
                ListItem(
                    headlineContent = { Text(text = "Achievement") },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24),
                            contentDescription = "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.clickable() {
                        //button function here
                        navController.navigate("achievement") {
                            launchSingleTop
                        }

                    })
                ListItem(
                    headlineContent = { Text(text = "Reservation history") },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24),
                            contentDescription = "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.clickable() {
                        //button function here
                    })
                ListItem(
                    headlineContent = { Text(text = "Change password") },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_24dp_cccccc_fill0_wght400_grad0_opsz24),
                            contentDescription = "Forward"
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.clickable() {
                        //button function here
                    }
                )

                ListItem(
                    headlineContent = {
                        Text(text = "Logout", color = MaterialTheme.colorScheme.error)
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.clickable {
                        scope.launch {
                            try {
                                // 1. 清除本地缓存
                                userPreferencesManager.clear()
                                // 2. 登出 Supabase
                                authRepository.logout()
                                // 3. 跳转登录页，清空返回栈
                                navController.navigate("LOGIN") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )

            }
        }
    }
}//Profile screen end