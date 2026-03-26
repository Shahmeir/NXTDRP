package com.example.nxtdrp

import android.os.CountDownTimer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.LocalTime
import java.time.ZoneId


    @Composable
    fun timer(Time: Long): String {

        var timeLeft = remember { mutableStateOf<String>("It is released already!!") }
        DisposableEffect(Time) {
        val countdown = object: CountDownTimer(Time, 1000) {
            var minute_holder: Long = -1;
            var hour_holder: Long = -1;
            var day_holder: Long = -1;
            override fun onTick(millisUntilFinished: Long) {
                // used a little ai to clean up the second, minute, hour math bc i was getting extra decimals
                val seconds = (millisUntilFinished % 60000) / 1000
                if (minute_holder == ((-1).toLong()) || seconds == 59.toLong()){
                    minute_holder = (millisUntilFinished % 3600000) / 60000
                }
                if (hour_holder.toInt() == (-1) || (seconds.toInt() == 59 && minute_holder.toInt() == 59)){
                    hour_holder = (millisUntilFinished % 86400000) / 3600000
                }
                if (day_holder.toInt() == -1 || (hour_holder.toInt() == 23 && seconds.toInt() == 59 && minute_holder.toInt() == 59))
                {
                    day_holder = millisUntilFinished / 86400000
                }
                timeLeft.value = "there are:\n${seconds} seconds\n${minute_holder} minutes\n${hour_holder} hours\n${day_holder} days left"
            }

            override fun onFinish() {

            }
        }.start()
            onDispose {
                countdown?.cancel()
            }
        }
        return timeLeft.value
    }
    //https://www.baeldung.com/kotlin/split-string
    //https://medium.com/@juricavoda/how-to-work-with-dates-and-time-in-kotlin-with-the-java-time-api-14767ed9c6f2
    fun StringToMSFromNow(stringDate: String) : Long{
        var listOfTime = stringDate.split('-')
        var myEmptyList = mutableListOf<Int>()
        listOfTime.forEach {myEmptyList.add(myEmptyList.size, it.toInt())}
        val releaseDate = LocalDate.of(myEmptyList[0], myEmptyList[1], myEmptyList[2])
        val now = LocalDate.now()
        //ai assisted here
        return ChronoUnit.MILLIS.between(now.atStartOfDay(), releaseDate.atStartOfDay())
    }
