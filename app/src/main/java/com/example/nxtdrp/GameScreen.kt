package com.example.nxtdrp

import android.content.Context
import android.icu.text.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.autofill.ContentDataType.Companion.Date
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

//used a bit of ai and https://medium.com/@pritam.karmahapatra/retrofit-in-android-with-kotlin-9af9f66a54a8 to create the api call set up
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
private fun getGames(onResult: (List<Game>) -> Unit)  {

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
                onResult(games)
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
    var games = remember { mutableStateListOf<Game>() }
    val state = rememberScrollState()
    LaunchedEffect(Unit) {getGames ({ result ->
        games.clear()
        games.addAll(result)
    })}
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .size(100.dp)
            .verticalScroll(state),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)
            val context = LocalContext.current
            Button(
                onClick = { fileReader(context, "test.txt")},
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("get the games")
            }
            games.forEach { game -> CountdownCard(game.name, "game", game.released) }


        }

    }
}
@Composable
private fun CountdownCard(title: String, category: String, timeLeft: String) {
    val context = LocalContext.current  // <-- add this
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
                onClick = { writeToFile(context, "test.txt", "$title{}$category{}$timeLeft") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("add to your page?")
            }        }
    }
}

fun writeToFile(context: Context, fileName: String, data: String) {
    try {
        val read = context.openFileInput(fileName).bufferedReader().useLines { lines ->
            lines.joinToString("\n")
        }
        if (read.contains(data))
        {            println("the goat")}

        else {
            context.openFileOutput(fileName, Context.MODE_APPEND).use {
                it.write(("\n$data").toByteArray())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun readFromFile(context: Context, fileName: String): String {
    return try {
        context.openFileInput(fileName).bufferedReader().useLines { lines ->
            lines.joinToString("\n")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error reading file"
    }
}

fun fileReader(context: Context, fileName: String)
{
    println(readFromFile(context, fileName))
}
