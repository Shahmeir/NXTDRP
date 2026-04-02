package com.example.nxtdrp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitInstanceRAWG {
    private const val BASE_URL = "https://api.rawg.io/api/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
public var now = getTimeForRAWG()
interface ApiInterfaceRAWG {
    @GET("games")
    fun getGames(
        @Query("key") apiKey: String,
        @Query("dates") dates: String,
        @Query("page_size") pageSize: Int
    ): Call<GameResponse>
}

data class GameResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Game>
)

data class Game(
    val name: String,
    val released: String
)

private fun getGames(onResult: (List<Game>) -> Unit) {
    val apiInterface = RetrofitInstanceRAWG.getInstance().create(ApiInterfaceRAWG::class.java)
    val call = apiInterface.getGames(
        apiKey = "97199ff1eb5c4a5eae165d148be56fbb",
        dates = getTimeForRAWG(),
        pageSize = 50
    )

    call.enqueue(object : Callback<GameResponse> {
        override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
            if (response.isSuccessful && response.body() != null) {
                val games = response.body()!!.results
                onResult(games)
            }
        }

        override fun onFailure(call: Call<GameResponse>, t: Throwable) {
            println("there was a failure in the api call")
        }
    })
}

private fun saveGameToFirestore(title: String, category: String, date: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return

    val gameData = hashMapOf(
        "title" to title,
        "category" to category,
        "date" to date
    )

    db.collection("users")
        .document(userId)
        .collection("savedItems")
        .document("games")
        .collection("items")
        .document(title)
        .set(gameData)
        .addOnSuccessListener {
            println("Game saved to Firestore: $title")
        }
        .addOnFailureListener { e ->
            println("Error saving game: ${e.message}")
        }
}

@Composable
fun GameScreen(navController: NavHostController) {
    var games = remember { mutableStateListOf<Game>() }
    val state = rememberScrollState()

    LaunchedEffect(Unit) {
        getGames { result ->
            games.clear()
            games.addAll(result)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .size(100.dp)
                .verticalScroll(state),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Games", style = MaterialTheme.typography.headlineMedium)

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }

            games.forEach { game ->
                GameCountdownCard(game.name, "game", game.released)
            }
        }
    }
}

@Composable
private fun GameCountdownCard(title: String, category: String, timeLeft: String) {
    var added by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(category, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Release date: $timeLeft", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = {
                    saveGameToFirestore(title, category, timeLeft)
                    added = true
                },
                enabled = !added,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (added) "Added!" else "Add to your page?")
            }
        }
    }
}