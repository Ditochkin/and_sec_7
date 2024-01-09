import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.AndroidViewModel
import com.example.and_sec_7.g_mainActivity
import java.time.Instant
import java.time.LocalDate

data class Date(
    var year: Int,
    var month: Int,
    var day: Int
)

class StepsInfoViewModel(app: Application): AndroidViewModel(app) {
    private val today: LocalDate = LocalDate.now()
    private var startDate: Date = Date(year = today.year, month = today.monthValue, day = today.dayOfMonth)
    private var endDate: Date = Date(year = today.year, month = today.monthValue, day = today.dayOfMonth)

    fun setStartDate(year: Int, month: Int, day: Int)
    {
        startDate.year = year
        startDate.month = month
        startDate.day = day
    }

    fun setEndDate(year: Int, month: Int, day: Int)
    {
        endDate.year = year
        endDate.month = month
        endDate.day = day
    }

    fun getStartDate() : Date
    {
        return startDate
    }

    fun getEndDate() : Date
    {
        return endDate
    }

    fun readStepsByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        onStepCountUpdated: (Int) -> Unit
    )
    {
        g_mainActivity.readStepsByTimeRange(healthConnectClient, startTime, endTime, onStepCountUpdated)
    }
}