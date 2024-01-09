package com.example.and_sec_7.ui.Screens

import StepsInfoViewModel
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.and_sec_7.g_healthConnectClient
import com.example.and_sec_7.g_numSteps
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.util.Calendar

@Composable
fun StepInfoScreen(
    stepsInfoViewModel: StepsInfoViewModel = viewModel(),
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isStartDate by remember { mutableStateOf(false) }
        var isEndDate by remember { mutableStateOf(false) }
        var steps by remember { mutableStateOf(0) }

        println("========= JETPACK COMPOSE =======")
        if (isStartDate)
        {
            showDatePicker(
                true,
                stepsInfoViewModel,
                onDateSelected = {
                isStartDate = false
                isEndDate = false
            })
        }
        else if (isEndDate)
        {
            showDatePicker(
                false,
                stepsInfoViewModel,
                onDateSelected = {
                isStartDate = false
                isEndDate = false
            })
        }
        else
        {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "${stepsInfoViewModel.getStartDate().year} ${stepsInfoViewModel.getStartDate().month} ${stepsInfoViewModel.getStartDate().day}"
                    , fontSize = 35.sp)
                Text(text = "${stepsInfoViewModel.getEndDate().year} ${stepsInfoViewModel.getEndDate().month} ${stepsInfoViewModel.getEndDate().day}"
                    , fontSize = 35.sp)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(onClick = {
                    isStartDate = true
                }) {
                    Text("Start date", fontSize = 25.sp)
                }

                Button(onClick = { isEndDate = true }) {
                    Text("End date", fontSize = 25.sp)
                }
            }

            Button(onClick = {
                val customStartDate = LocalDateTime.of(stepsInfoViewModel.getStartDate().year, stepsInfoViewModel.getStartDate().month, stepsInfoViewModel.getStartDate().day, 0, 0) // Здесь установите нужную вам дату и время
                val customStartInstant = customStartDate.atZone(ZoneId.systemDefault()).toInstant()

                val customEndDate = LocalDateTime.of(stepsInfoViewModel.getEndDate().year, stepsInfoViewModel.getEndDate().month, stepsInfoViewModel.getEndDate().day, 23, 59) // Здесь установите нужную вам дату и время
                val customEndInstant = customEndDate.atZone(ZoneId.systemDefault()).toInstant()

                stepsInfoViewModel.readStepsByTimeRange(g_healthConnectClient, customStartInstant, customEndInstant
                ) {newStepCount ->
                    steps = newStepCount
                }
            }) {
                Text("Count steps", fontSize = 25.sp)
            }

            Text(text = "Steps:",
                fontSize = 30.sp,
                modifier = Modifier.padding(top = 30.dp))

            Text(text = "$steps",
                fontSize = 30.sp)
        }
    }
}

@Composable
fun showDatePicker(
    isStartDate : Boolean,
    stepsInfoViewModel: StepsInfoViewModel = viewModel(),
    onDateSelected: (Long) -> Unit,
) {
    val calendar = Calendar.getInstance()
    AndroidView(
        { context ->
            DatePicker(context).apply {
                setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                    println("${year}  ${monthOfYear} ${dayOfMonth}")
                    calendar.set(year, monthOfYear, dayOfMonth)

                    if (isStartDate)
                    {
                        stepsInfoViewModel.setStartDate(year, monthOfYear + 1, dayOfMonth)
                    }
                    else
                    {
                        stepsInfoViewModel.setEndDate(year, monthOfYear + 1, dayOfMonth)
                    }

                    val selectedDate = calendar.timeInMillis
                    onDateSelected(selectedDate)
                }
            }
        },
        modifier = Modifier.wrapContentWidth()
    )
}