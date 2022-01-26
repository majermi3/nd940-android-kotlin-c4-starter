package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.LinkedHashMap
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    private val reminder1 = ReminderDataItem("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
    private val reminder2 = ReminderDataItem(null, "Description 2", "Location 1", 48.2, 50.2, "reminder2")
    private val reminder3 = ReminderDataItem("Reminder 2", "Description 3", null, 48.3, 50.3, "reminder3")
    private val reminders = LinkedHashMap<String, ReminderDTO>()

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource(reminders)
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @Test
    fun validateEnteredData_withValidReminder_hasNoError() {
        // When valid data are passed to validateEnteredData
        val result = saveReminderViewModel.validateEnteredData(reminder1)

        // Then there are no errors
        assertTrue(result)
        assertThat(saveReminderViewModel.showSnackBarInt.value, nullValue())
    }

    @Test
    fun validateEnteredData_withReminderWithoutTitle_hasError() {
        // When null title is validated
        val result = saveReminderViewModel.validateEnteredData(reminder2)

        // Then there is error
        assertFalse(result)
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_withReminderWithoutLocation_hasError() {
        // When null location is validated
        val result = saveReminderViewModel.validateEnteredData(reminder3)

        // Then there is error
        assertFalse(result)
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun onClear_withSetValues_clearsAllValues() {
        // When all values are set
        saveReminderViewModel.reminderTitle.value = "title"
        saveReminderViewModel.reminderDescription.value = "description"
        saveReminderViewModel.reminderSelectedLocationStr.value = "selected location"
        saveReminderViewModel.selectedPOI.value = PointOfInterest(LatLng(41.1, 51.1), "test", "test")
        saveReminderViewModel.latitude.value = 41.1
        saveReminderViewModel.longitude.value = 51.1

        // Then onClear is called
        saveReminderViewModel.onClear()

        // All values have null values
        assertThat(saveReminderViewModel.reminderTitle.value, nullValue())
        assertThat(saveReminderViewModel.reminderDescription.value, nullValue())
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, nullValue())
        assertThat(saveReminderViewModel.selectedPOI.value, nullValue())
        assertThat(saveReminderViewModel.latitude.value, nullValue())
        assertThat(saveReminderViewModel.longitude.value, nullValue())
    }

    @Test
    fun saveReminder_withValidReminder_addsReminderToDataSourceAndShowsToast() = runBlockingTest {
        // When saveReminder with reminder1 is called
        saveReminderViewModel.saveReminder(reminder1)

        // Then dataSource has size 1 and first element has correct data
        assertThat(dataSource.reminders.size, `is`(1))

        val reminderFromDataSource = dataSource.getReminder(reminder1.id)
        assertTrue(reminderFromDataSource is Result.Success)

        val successReminder = reminderFromDataSource as Result.Success
        assertThat(successReminder.data.title, `is`(reminders[reminder1.id]!!.title))
        assertThat(successReminder.data.description, `is`(reminders[reminder1.id]!!.description))
        assertThat(successReminder.data.location, `is`(reminders[reminder1.id]!!.location))
        assertThat(successReminder.data.longitude, `is`(reminders[reminder1.id]!!.longitude))
        assertThat(successReminder.data.latitude, `is`(reminders[reminder1.id]!!.latitude))

        assertThat(saveReminderViewModel.showToast.value, `is`("Reminder Saved !"))
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}