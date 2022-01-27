package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.junit.Assert.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private val reminder1 = ReminderDTO("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
    private val reminder1Update = ReminderDTO("Reminder 2", "Description 2", "Location 2", 48.2, 50.2, "reminder1")
    private val reminder2 = ReminderDTO("Reminder 2", "Description 2", "Location 2", 48.2, 50.2, "reminder2")
    
    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getReminder_withInvalidId_returnsError() = runBlocking {
        val result = remindersLocalRepository.getReminder(reminder1.id)
        assertTrue(result is Result.Error)
        val message = (result as Result.Error).message
        assertThat(message, `is`("Reminder not found!"))
    }

    @Test
    fun saveReminderAndGetReminderById() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)

        val result = remindersLocalRepository.getReminder(reminder1.id)

        assertTrue(result is Result.Success)
        val dbReminder = (result as Result.Success).data

        assertThat(dbReminder.title, `is`(reminder1.title))
        assertThat(dbReminder.description, `is`(reminder1.description))
        assertThat(dbReminder.location, `is`(reminder1.location))
        assertThat(dbReminder.longitude, `is`(reminder1.longitude))
        assertThat(dbReminder.latitude, `is`(reminder1.latitude))
    }

    @Test
    fun replaceExistingReminder() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder1Update)

        val result = remindersLocalRepository.getReminder(reminder1.id)

        assertTrue(result is Result.Success)
        val dbReminder = (result as Result.Success).data

        assertThat(dbReminder.title, `is`(reminder1Update.title))
        assertThat(dbReminder.description, `is`(reminder1Update.description))
        assertThat(dbReminder.location, `is`(reminder1Update.location))
        assertThat(dbReminder.longitude, `is`(reminder1Update.longitude))
        assertThat(dbReminder.latitude, `is`(reminder1Update.latitude))
    }

    @Test
    fun deleteAllReminders() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)

        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminders()

        assertTrue(result is Result.Success)
        val reminders = (result as Result.Success).data

        assertThat(reminders.size, `is`(0))
    }

    @Test
    fun getAllReminders() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)

        val result = remindersLocalRepository.getReminders()

        assertTrue(result is Result.Success)
        val reminders = (result as Result.Success).data

        assertThat(reminders.size, `is`(2))

        assertThat(reminders[0].title, `is`(reminder1.title))
        assertThat(reminders[0].description, `is`(reminder1.description))
        assertThat(reminders[0].location, `is`(reminder1.location))
        assertThat(reminders[0].longitude, `is`(reminder1.longitude))
        assertThat(reminders[0].latitude, `is`(reminder1.latitude))

        assertThat(reminders[1].title, `is`(reminder2.title))
        assertThat(reminders[1].description, `is`(reminder2.description))
        assertThat(reminders[1].location, `is`(reminder2.location))
        assertThat(reminders[1].longitude, `is`(reminder2.longitude))
        assertThat(reminders[1].latitude, `is`(reminder2.latitude))
    }
}