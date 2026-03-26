package com.example.nxtdrp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun HomePage(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    var notificationsEnabled by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Default Light") }

    var savedGames by remember { mutableStateOf<List<CountdownItem>>(emptyList()) }
    var savedMusic by remember { mutableStateOf<List<CountdownItem>>(emptyList()) }

    val scrollState = rememberScrollState()

    DisposableEffect(user?.uid) {
        val listeners = mutableListOf<ListenerRegistration>()

        if (user != null) {

            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        notificationsEnabled =
                            document.getBoolean("notifications") ?: false
                        selectedTheme =
                            document.getString("theme") ?: "Default Light"
                    }
                }

            // Listen for saved games in real time
            val gamesListener = db.collection("users")
                .document(user.uid)
                .collection("savedItems")
                .document("games")
                .collection("items")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        println("Error listening to games: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val items = mutableListOf<CountdownItem>()
                        for (doc in snapshots) {
                            val title = doc.getString("title") ?: ""
                            val category = doc.getString("category") ?: ""
                            val date = doc.getString("date") ?: ""
                            items.add(CountdownItem(title = title, category = category, date = date))
                        }
                        savedGames = items
                    }
                }
            listeners.add(gamesListener)

            // Listen for saved music in real time
            val musicListener = db.collection("users")
                .document(user.uid)
                .collection("savedItems")
                .document("music")
                .collection("items")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        println("Error listening to music: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val items = mutableListOf<CountdownItem>()
                        for (doc in snapshots) {
                            val title = doc.getString("title") ?: ""
                            val category = doc.getString("category") ?: ""
                            val date = doc.getString("date") ?: ""
                            items.add(CountdownItem(title = title, category = category, date = date))
                        }
                        savedMusic = items
                    }
                }
            listeners.add(musicListener)
        }

        onDispose {
            listeners.forEach { it.remove() }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            // ── Header section ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Text(
                        text = "NXTDRP",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Welcome back, ${user?.email?.substringBefore("@") ?: ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick stats row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatChip(
                            label = "${savedGames.size} Games",
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                        StatChip(
                            label = "${savedMusic.size} Music",
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── Navigation row ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NavButton(
                        text = "Games",
                        onClick = { navController.navigate("games") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavButton(
                        text = "Music",
                        onClick = { navController.navigate("music") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Settings")
                    }

                    OutlinedButton(
                        onClick = {
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Logout")
                    }
                }

                // ── Games section ──
                if (savedGames.isNotEmpty()) {
                    SectionHeader(title = "Games")

                    savedGames.forEach { item ->
                        var time = timerStarter(item.date)
                        CountdownCard(
                            title = item.title,
                            category = item.category,
                            timeLeft = time,
                            accentColor = MaterialTheme.colorScheme.primary,
                            onRemove = { removeFromFirestore(item.title, "games") }
                        )
                    }
                }

                // ── Music section ──
                if (savedMusic.isNotEmpty()) {
                    SectionHeader(title = "Music")

                    savedMusic.forEach { item ->
                        var time = timerStarter(item.date)
                        CountdownCard(
                            title = item.title,
                            category = item.category,
                            timeLeft = time,
                            accentColor = MaterialTheme.colorScheme.secondary,
                            onRemove = { removeFromFirestore(item.title, "music") }
                        )
                    }
                }

                // ── Empty state ──
                if (savedGames.isEmpty() && savedMusic.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nothing tracked yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Browse Games or Music to start your countdown feed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class CountdownItem(
    val title: String = "",
    val category: String = "",
    val date: String = ""
)

private fun removeFromFirestore(title: String, collection: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return

    db.collection("users")
        .document(userId)
        .collection("savedItems")
        .document(collection)
        .collection("items")
        .document(title)
        .delete()
        .addOnSuccessListener {
            println("Removed from Firestore: $title")
        }
        .addOnFailureListener { e ->
            println("Error removing: ${e.message}")
        }
}

// ── Composables ──

@Composable
private fun StatChip(
    label: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}

@Composable
private fun NavButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = colors
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
private fun CountdownCard(
    title: String,
    category: String,
    timeLeft: String,
    accentColor: Color,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Release date: $timeLeft",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

}

@Composable
private fun timerStarter(releaseDate: String): String {
    var msToRelease = StringToMSFromNow(releaseDate)
    return timer(msToRelease)
}