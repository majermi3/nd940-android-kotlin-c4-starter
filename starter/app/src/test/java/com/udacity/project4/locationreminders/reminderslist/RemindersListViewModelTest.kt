package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.LinkedHashMap
import org.hamcrest.Matchers.*
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    private val reminder1 = ReminderDTO("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
    private val reminder2 = ReminderDTO("Reminder 2", "Description 2", "Location 2", 48.2, 50.2, "reminder2")
    private val reminder3 = ReminderDTO("Reminder 3", "Description 3", "Location 3", 48.3, 50.3, "reminder3")
    private val reminders = LinkedHashMap<String, ReminderDTO>()

    private lateinit var dataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Test
    fun loadReminders_setsReminderList() {
        // When reminders are added to the list
        reminders[reminder1.id] = reminder1
        reminders[reminder2.id] = reminder2
        reminders[reminder3.id] = reminder3

        dataSource = FakeDataSource(reminders)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
        // Then loadReminders is called
        reminderListViewModel.loadReminders()

        // Verify that reminderList has correct data and there is no error
        assertThat(reminderListViewModel.remindersList.value, not(nullValue()))
        assertThat(reminders.size, `is`(reminderListViewModel.remindersList.value?.size))

        assertThat(reminderListViewModel.remindersList.value?.get(0)?.title, `is`(reminders[reminder1.id]!!.title))
        assertThat(reminderListViewModel.remindersList.value?.get(1)?.title, `is`(reminders[reminder2.id]!!.title))
        assertThat(reminderListViewModel.remindersList.value?.get(2)?.title, `is`(reminders[reminder3.id]!!.title))

        assertThat(reminderListViewModel.remindersList.value?.get(0)?.description, `is`(reminders[reminder1.id]!!.description))
        assertThat(reminderListViewModel.remindersList.value?.get(1)?.description, `is`(reminders[reminder2.id]!!.description))
        assertThat(reminderListViewModel.remindersList.value?.get(2)?.description, `is`(reminders[reminder3.id]!!.description))

        assertThat(reminderListViewModel.remindersList.value?.get(0)?.location, `is`(reminders[reminder1.id]!!.location))
        assertThat(reminderListViewModel.remindersList.value?.get(1)?.location, `is`(reminders[reminder2.id]!!.location))
        assertThat(reminderListViewModel.remindersList.value?.get(2)?.location, `is`(reminders[reminder3.id]!!.location))

        assertThat(reminderListViewModel.remindersList.value?.get(0)?.longitude, `is`(reminders[reminder1.id]!!.longitude))
        assertThat(reminderListViewModel.remindersList.value?.get(1)?.longitude, `is`(reminders[reminder2.id]!!.longitude))
        assertThat(reminderListViewModel.remindersList.value?.get(2)?.longitude, `is`(reminders[reminder3.id]!!.longitude))

        assertThat(reminderListViewModel.remindersList.value?.get(0)?.latitude, `is`(reminders[reminder1.id]!!.latitude))
        assertThat(reminderListViewModel.remindersList.value?.get(1)?.latitude, `is`(reminders[reminder2.id]!!.latitude))
        assertThat(reminderListViewModel.remindersList.value?.get(2)?.latitude, `is`(reminders[reminder3.id]!!.latitude))

        assertThat(reminderListViewModel.showSnackBar.value, nullValue())
    }

    @Test
    fun loadReminders_withEmptyData_ShowSnackBarHasErrorNoData() {
        // When no reminders are added to the list
        dataSource = FakeDataSource(reminders)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )

        // Then loadReminders is called
        reminderListViewModel.loadReminders()

        // Verify that error massage is in the snackBar
        assertThat(reminderListViewModel.showSnackBar.value, `is`("No Data"))
    }
}