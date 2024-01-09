package com.example.and_sec_7.ui.Screens.AddSteps

import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.AndroidViewModel
import com.example.and_sec_7.g_mainActivity
import com.example.and_sec_7.ui.Screens.StepInfo.Date
import java.time.Instant
import java.time.LocalDate

data class Time(
    var hour: Int,
    var minute: Int
)
class AddStepsViewModel(app: Application): AndroidViewModel(app) {
    private val today: LocalDate = LocalDate.now()
    private var date: Date = Date(year = today.year, month = today.monthValue, day = today.dayOfMonth)
    private var startTime: Time = Time(hour = 0, minute = 0)
    private var endTime: Time = Time(hour = 0, minute = 0)

    fun setDate(year: Int, month: Int, day: Int)
    {
        date.year = year
        date.month = month
        date.day = day
    }

    fun setStartTime(hour: Int, minute: Int)
    {
        startTime.hour = hour
        startTime.minute = minute
    }

    fun setEndTime(hour: Int, minute: Int)
    {
        endTime.hour = hour
        endTime.minute = minute
    }

    fun getDate() : Date
    {
        return date
    }

    fun getStartTime() : Time
    {
        return startTime
    }

    fun getEndTime() : Time
    {
        return endTime
    }
}