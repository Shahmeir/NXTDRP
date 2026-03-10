package com.example.nxtdrp

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.internal.ApiKey
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


//used the game page to set this up
object RetrofitInstanceMBZ{
    private const val BASE_URL ="https://musicbrainz.org/"


    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient().build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
//used ai to help clean up this request i found from https://medium.com/@1550707241489/how-to-add-headers-to-retrofit-android-kotlin-450da34d3c3a
private fun okHttpClient() = OkHttpClient().newBuilder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "NXTDRP hhh.heaslip@gmail.com")
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }
interface ApiInterface {
    @GET("ws/2/release")
    fun getMusic(
        @Query("query") dates: String,
        @Query("fmt") format: String
    ): Call<MusicResponce>
}
data class MusicResponce(
    val releases: List<Music>
)

data class Music(
    val title: String,
    val date: String
)
private fun getMusic(onResult: (List<Music>) -> Unit)  {

    val apiInterface = RetrofitInstanceMBZ.getInstance().create(ApiInterface::class.java)
    val call = apiInterface.getMusic(
        dates = "date:2026",
        format = "json"
    )

    call.enqueue(object : Callback<MusicResponce> {

        override fun onResponse(call: Call<MusicResponce>, response: Response<MusicResponce>) {
            println(response)

            if (response.isSuccessful && response.body() != null) {
                val games = response.body()!!.releases  // this is your list

                for (game in games) {
                    println(game.title)
                    println(game.date)
                }
                onResult(games)
            }
            else {
                println(response)
            }
        }

        override fun onFailure(call: Call<MusicResponce>, t: Throwable) {
            println("there was a failure in the music api call")
            println(call.toString())
        }
    })
}
@Composable
fun MusicScreen(navController: NavHostController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Default Light") }
    var games = remember { mutableStateListOf<Music>() }
    val state = rememberScrollState()
    LaunchedEffect(Unit) {getMusic ({ result ->
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
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
            Button(
                onClick = { fileReader2(context, "music.txt")},
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("get the games")
            }
            games.forEach { game -> CountdownCard(game.title, "music", game.date) }




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
                onClick = { writeToFile2(context, "music.txt", "$title{}$category{}$timeLeft") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("add to your page?")
            }        }
    }
}

fun writeToFile2(context: Context, fileName: String, data: String) {
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
fun readFromFile2(context: Context, fileName: String): String {
    return try {
        context.openFileInput(fileName).bufferedReader().useLines { lines ->
            lines.joinToString("\n")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error reading file"
    }
}

fun fileReader2(context: Context, fileName: String)
{
    println(readFromFile2(context, fileName))
}
