package com.example.and_sec_7

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.platform.client.proto.PermissionProto
import com.example.and_sec_7.ui.theme.And_sec_7Theme
import java.security.Permission
import java.time.Instant
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.and_sec_7.navigation.AppNavHost
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset

lateinit var g_mainActivity : MainActivity
lateinit var g_healthConnectClient : HealthConnectClient
var g_numSteps = 0

class MainActivity : ComponentActivity() {
    val PERMISSIONS =
        setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )

    var steptsItems = ""

    // Create the permissions launcher
    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

    val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
        if (granted.containsAll(PERMISSIONS)) {
            println("Permissions successfully granted")
            // Permissions successfully granted
        } else {
            println("Lack of required permissions")
            // Lack of required permissions
        }
    }

    suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(PERMISSIONS)) {
            println("All permissions accepted")
            // Permissions already granted; proceed with inserting or reading data
        } else {
            println("Request permissions")
            requestPermissions.launch(PERMISSIONS)
        }

//        val customDate = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0) // Здесь установите нужную вам дату и время
//        val customInstant = customDate.atZone(ZoneId.systemDefault()).toInstant()
//
//        val startTime = Instant.ofEpochSecond(20000)
//        val endTime = Instant.ofEpochSecond(0)
//        readStepsByTimeRange(healthConnectClient, customInstant, Instant.now())
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthConnectClient = HealthConnectClient.getOrCreate(this)

        g_mainActivity = this
        g_healthConnectClient = healthConnectClient

        lifecycleScope.launch {
            checkPermissionsAndRun(healthConnectClient)
        }

        setContent {
            And_sec_7Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }

    fun readStepsByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        onStepCountUpdated: (Int, String) -> Unit
    ) {
        lifecycleScope.launch {
            readStepsByTimeRangeThread(healthConnectClient, startTime, endTime)
            onStepCountUpdated(g_numSteps, steptsItems)
        }
    }

    suspend fun readStepsByTimeRangeThread(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        println("readStepsByTimeRangeThread")
        try {
            println("Before response")
            val response =
                healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
            println(response.records.count())
            g_numSteps = 0
            steptsItems = ""
            for (stepRecord in response.records) {
                // Process each step record
                steptsItems += "Timestamp: (${stepRecord.startTime} - ${stepRecord.endTime}): ${stepRecord.count}\n"
                println("Step Count: ${stepRecord.count}, Timestamp: ${stepRecord.endTime}")
                g_numSteps += stepRecord.count.toInt()
            }
        } catch (e: Exception) {
            // Run error handling here.
            println("Run error handling here.")
        }
    }

    fun insertStepsThread(startTime: Instant, endTime: Instant, steps : Long){
        lifecycleScope.launch {
            insertSteps(g_healthConnectClient, startTime, endTime, steps)
        }
    }
    suspend fun insertSteps(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant, steps : Long) {
        try {
            val stepsRecord = StepsRecord(
                count = steps,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZoneOffset.MAX,
                endZoneOffset = ZoneOffset.MAX,
            )
            healthConnectClient.insertRecords(listOf(stepsRecord))
        } catch (e: Exception) {
            // Run error handling here
        }
    }
}

@Composable
fun Greeting(context : Context,name: String, modifier: Modifier = Modifier) {

    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}