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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitInstanceMBZ {
    private const val BASE_URL = "https://musicbrainz.org/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient().build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}

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

private fun getMusic(onResult: (List<Music>) -> Unit) {
    val apiInterface = RetrofitInstanceMBZ.getInstance().create(ApiInterface::class.java)
    val call = apiInterface.getMusic(
        dates = "date:[${getNowForMusic()}]",
        format = "json"
    )

    call.enqueue(object : Callback<MusicResponce> {
        override fun onResponse(call: Call<MusicResponce>, response: Response<MusicResponce>) {
            if (response.isSuccessful && response.body() != null) {
                val music = response.body()!!.releases
                println("stuff wourks")

                println("stuff wourks")
                onResult(music)
            } else {
                println(response)

            }
        }

        override fun onFailure(call: Call<MusicResponce>, t: Throwable) {
            println("there was a failure in the music api call")
            println(call.toString())
        }
    })
}

private fun saveMusicToFirestore(title: String, category: String, date: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return

    val musicData = hashMapOf(
        "title" to title,
        "category" to category,
        "date" to date
    )

    db.collection("users")
        .document(userId)
        .collection("savedItems")
        .document("music")
        .collection("items")
        .document(title)
        .set(musicData)
        .addOnSuccessListener {
            println("Music saved to Firestore: $title")
        }
        .addOnFailureListener { e ->
            println("Error saving music: ${e.message}")
        }
}

@Composable
fun MusicScreen(navController: NavHostController) {
    var music = remember { mutableStateListOf<Music>() }
    val state = rememberScrollState()

    LaunchedEffect(Unit) {
        getMusic { result ->
            music.clear()
            music.addAll(result)
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
            Text("Music", style = MaterialTheme.typography.headlineMedium)

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }

            music.forEach { item ->
                MusicCountdownCard(item.title, "music", item.date)
            }
        }
    }
}

@Composable
private fun MusicCountdownCard(title: String, category: String, timeLeft: String) {
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
                    saveMusicToFirestore(title, category, timeLeft)
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