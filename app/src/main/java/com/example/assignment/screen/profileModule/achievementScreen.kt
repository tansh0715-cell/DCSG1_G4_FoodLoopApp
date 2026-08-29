package com.example.assignment.screen.profileModule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.model.Achievement


@Composable
fun AchievementScreen(
    innerPadding: PaddingValues
){
    val achievementList = mutableListOf<Achievement>(
        Achievement("First Food Rescue","Buy 1 surplus meal","\uD83C\uDFC5","You rescued your first surplus meal and prevented it from becoming waste.",1,1),
        Achievement("Waste Warrior","Buy 5 surplus meal","\uD83C\uDF31","Five meals saved from the bin. Small actions, real impact.",5,5),
        Achievement("Food Hero","Buy 10 surplus meal","❤\uFE0F","",7,10),
        Achievement("Planet Protector","Buy 50 surplus meal","\uD83C\uDF0D","",7,50)
    )
    Column(modifier = Modifier.fillMaxWidth().padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Achievements", style = MaterialTheme.typography.headlineLarge)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievementList){achievement ->
                ElevatedCard(modifier = Modifier.fillMaxWidth().height(125.dp)) {
                    val progress = achievement.current.toFloat() / achievement.target.toFloat()
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                        Column(Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = achievement.icon,fontSize = 40.sp)

                        }
                        Column(Modifier.weight(2f).padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(text = achievement.title, style = MaterialTheme.typography.labelLarge)
                            if (achievement.current == achievement.target){
                                Text(
                                    text = achievement.quote,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                )}
                            else
                            {
                                Text(text = achievement.description, style = MaterialTheme.typography.bodyMedium
                                    , color = MaterialTheme.colorScheme.onSecondary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start) {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.width(150.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.background,
                                    drawStopIndicator = {}
                                )
                                Text(
                                    text = "${achievement.current}/${achievement.target}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
