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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment.viewmodel.achievement.AchievementViewModel

@Composable
fun AchievementScreen(
    innerPadding: PaddingValues,
    viewModel: AchievementViewModel
) {

    val achievements by
    viewModel.achievements
        .collectAsStateWithLifecycle()

    val isLoading by
    viewModel.isLoading
        .collectAsStateWithLifecycle()

    val error by
    viewModel.error
        .collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {

        viewModel.loadAchievements()

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {



        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                CircularProgressIndicator()

            }

        } else if (error != null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = error ?: "Unknown error",
                    color =
                        MaterialTheme.colorScheme.error
                )

            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    achievements
                ) { progress ->

                    val achievement =
                        progress.achievement

                    val current =
                        progress.current


                    // Prevent progress from exceeding 100%
                    val progressValue =
                        (
                                current.toFloat() /
                                        achievement.target.toFloat()
                                )
                            .coerceIn(0f, 1f)


                    ElevatedCard(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,

                            modifier =
                                Modifier.fillMaxHeight()
                        ) {

                            // ==========================
                            // Achievement icon
                            // ==========================

                            Column(
                                modifier =
                                    Modifier.weight(1f),

                                verticalArrangement =
                                    Arrangement.Center,

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text =
                                        achievement.icon,

                                    fontSize =
                                        40.sp
                                )
                            }


                            // ==========================
                            // Achievement information
                            // ==========================

                            Column(
                                modifier =
                                    Modifier
                                        .weight(2f)
                                        .padding(
                                            horizontal = 10.dp
                                        ),

                                horizontalAlignment =
                                    Alignment.Start,

                                verticalArrangement =
                                    Arrangement.spacedBy(5.dp)
                            ) {

                                Text(
                                    text =
                                        achievement.title,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelLarge
                                )


                                // Completed
                                if (
                                    current >=
                                    achievement.target
                                ) {

                                    Text(
                                        text =
                                            achievement.quote,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium,

                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .secondary
                                    )

                                } else {

                                    Text(
                                        text =
                                            achievement.description,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium,

                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSecondary
                                    )
                                }


                                // ==========================
                                // Progress bar
                                // ==========================

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    LinearProgressIndicator(

                                        progress = {
                                            progressValue
                                        },

                                        modifier =
                                            Modifier.width(
                                                150.dp
                                            ),

                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .primary,

                                        trackColor =
                                            MaterialTheme
                                                .colorScheme
                                                .background,

                                        drawStopIndicator = {}
                                    )


                                    Text(
                                        text =
                                            "${current}/${achievement.target}",

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall,

                                        modifier =
                                            Modifier.padding(
                                                horizontal = 12.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}