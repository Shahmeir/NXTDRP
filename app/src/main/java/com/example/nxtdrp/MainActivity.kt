package com.example.nxtdrp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.nxtdrp.ui.theme.NXTDRPTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            NXTDRPTheme {

                val navController = rememberNavController()

                // 🔥 Check if user already logged in
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
                        SettingScreen(navController)
                    }

                    composable("games") {
                        GameScreen(navController)
                    }
                    composable("signup") {
                        SignupScreen(navController)
                    }
                }
            }
        }
    }
}