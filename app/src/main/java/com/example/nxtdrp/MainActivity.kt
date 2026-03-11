package com.example.nxtdrp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.*
import com.example.nxtdrp.ui.theme.NXTDRPTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {

            var currentTheme by remember { mutableStateOf("Default Light") }

            // Load theme from Firestore on startup
            val user = auth.currentUser
            if (user != null) {
                androidx.compose.runtime.LaunchedEffect(user.uid) {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                currentTheme = document.getString("theme") ?: "Default Light"
                            }
                        }
                }
            }

            NXTDRPTheme(themeName = currentTheme) {

                val navController = rememberNavController()

                val startDestination = if (auth.currentUser != null) {
                    "home"
                } else {
                    "login"
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable("login") {
                        LoginScreen(navController)
                    }

                    composable("home") {
                        HomePage(navController)
                    }

                    composable("settings") {
                        SettingScreen(
                            navController = navController,
                            onThemeChanged = { newTheme ->
                                currentTheme = newTheme
                            }
                        )
                    }

                    composable("games") {
                        GameScreen(navController)
                    }

                    composable("signup") {
                        SignupScreen(navController)
                    }

                    composable("music") {
                        MusicScreen(navController)
                    }
                }
            }
        }
    }
}