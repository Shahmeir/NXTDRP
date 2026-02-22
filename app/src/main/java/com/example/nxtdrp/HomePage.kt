package com.example.nxtdrp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomePage(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Your Countdown Feed", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Demo items you’re excited for",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CountdownCard(title = "GTA VI", category = "Game", timeLeft = "3 months")
            CountdownCard(title = "New Album Drop", category = "Music", timeLeft = "12 days")
            CountdownCard(title = "Stranger Things", category = "Show", timeLeft = "6 weeks")

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Logout")
                }

                Button(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Settings")
                }

                Button(
                    onClick = { navController.navigate("games") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Games")
                }

            }
        }
    }
}

@Composable
private fun CountdownCard(title: String, category: String, timeLeft: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(category, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Time left: $timeLeft", style = MaterialTheme.typography.titleMedium)
        }
    }
}
