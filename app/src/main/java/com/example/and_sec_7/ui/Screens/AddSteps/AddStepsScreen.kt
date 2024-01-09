package com.example.and_sec_7.ui.Screens.AddSteps

import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.and_sec_7.g_healthConnectClient
import com.example.and_sec_7.g_mainActivity
import com.example.and_sec_7.navigation.Screens
import com.example.and_sec_7.ui.Screens.StepInfo.StepsInfoViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

@Composable
fun AddStepsScreen(
    addStepsViewModel: AddStepsViewModel = viewModel(),
    navController: NavController
) {
    var isSelectedDate by remember { mutableStateOf(false) }
    var isSelectedStartTime by remember { mutableStateOf(false) }
    var isSelectedEndTime by remember { mutableStateOf(false) }

    if (!isSelectedDate)
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(top = 250.dp, start = 20.dp, end = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            showDatePicker(
                addStepsViewModel,
                onDateSelected = {
                    isSelectedDate = true
                })
        }

    }
    else if (!isSelectedStartTime)
    {
        MyContent(true,
            addStepsViewModel,
            onTimeSelected = {
                isSelectedStartTime = true
            })
    }
    else if (!isSelectedEndTime)
    {
        MyContent(false,
            addStepsViewModel,
            onTimeSelected = {
                isSelectedEndTime = true
            })
    }
    else
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(top = 250.dp, start = 20.dp, end = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Date: ${addStepsViewModel.getDate().year}:${addStepsViewModel.getDate().month}:${addStepsViewModel.getDate().day}", fontSize = 30.sp)
            Text(text = "Start time: ${addStepsViewModel.getStartTime().hour}:${addStepsViewModel.getStartTime().minute}:00", fontSize = 30.sp)
            Text(text = "End time: ${addStepsViewModel.getEndTime().hour}:${addStepsViewModel.getEndTime().minute}:00", fontSize = 30.sp)

            val pattern = remember { Regex("^\\d+\$") }
            var text by remember { mutableStateOf("") }
            TextField(
                value = text,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        text = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Num steps") }
            )

            Button(onClick = {
                val customStartDate = LocalDateTime.of(addStepsViewModel.getDate().year,
                                                        addStepsViewModel.getDate().month,
                                                        addStepsViewModel.getDate().day,
                                                        addStepsViewModel.getStartTime().hour,
                                                        addStepsViewModel.getStartTime().minute)
                val customStartInstant = customStartDate.atZone(ZoneId.systemDefault()).toInstant()

                val customEndDate = LocalDateTime.of(addStepsViewModel.getDate().year,
                    addStepsViewModel.getDate().month,
                    addStepsViewModel.getDate().day,
                    addStepsViewModel.getEndTime().hour,
                    addStepsViewModel.getEndTime().minute)
                val customEndInstant = customEndDate.atZone(ZoneId.systemDefault()).toInstant()

                g_mainActivity.insertStepsThread(customStartInstant, customEndInstant, text.toLong())

                navController.navigate(Screens.StepsInfo.name)
            }) {
                Text("Add steps", fontSize = 12.sp)
            }
        }
    }

}

@Composable
fun showDatePicker(
    addStepsViewModel: AddStepsViewModel = viewModel(),
    onDateSelected: (Long) -> Unit,
) {
    val calendar = Calendar.getInstance()
    AndroidView(
        { context ->
            DatePicker(context).apply {
                setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                    println("${year}  ${monthOfYear} ${dayOfMonth}")
                    calendar.set(year, monthOfYear, dayOfMonth)

                    addStepsViewModel.setDate(year, monthOfYear + 1, dayOfMonth)

                    val selectedDate = calendar.timeInMillis
                    onDateSelected(selectedDate)
                }
            }
        },
        modifier = Modifier.wrapContentWidth()
    )
}

@Composable
fun MyContent(
    isStartedTime: Boolean,
    addStepsViewModel: AddStepsViewModel = viewModel(),
    onTimeSelected: (Long) -> Unit
){

    // Fetching local context
    val mContext = g_mainActivity

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }

    // Creating a TimePicker dialod
    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
            if (isStartedTime)
            {
                addStepsViewModel.setStartTime(mHour, mMinute)
            }
            else
            {
                addStepsViewModel.setEndTime(mHour, mMinute)
            }
            onTimeSelected(0)
        }, mHour, mMinute, false
    )

    mTimePickerDialog.show()
}
