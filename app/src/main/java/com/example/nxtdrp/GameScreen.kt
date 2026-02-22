package com.example.nxtdrp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Callback
import retrofit2.Response

object RetrofitInstanceRAWG{
    private const val BASE_URL ="https://api.rawg.io/api/"


    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}

interface ApiInterface {

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
private fun getGames() {
    val apiInterface = RetrofitInstanceRAWG.getInstance().create(ApiInterface::class.java)
    val call = apiInterface.getGames(
        apiKey = "97199ff1eb5c4a5eae165d148be56fbb",
        dates = "2026-02-01,2027-02-01",
        pageSize = 50
    )

    call.enqueue(object : Callback<GameResponse> {

        override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
            if (response.isSuccessful && response.body() != null) {
                val games = response.body()!!.results  // this is your list

                for (game in games) {
                    println(game.name)
                    println(game.released)
                }
            }
        }

        override fun onFailure(call: Call<GameResponse>, t: Throwable) {
            println("there was a failure in the api call")
        }
    })
}
@Composable
fun GameScreen(navController: NavHostController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Default Light") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)

            Button(
                onClick = {getGames() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("get the games")
            }

        }
    }
}
