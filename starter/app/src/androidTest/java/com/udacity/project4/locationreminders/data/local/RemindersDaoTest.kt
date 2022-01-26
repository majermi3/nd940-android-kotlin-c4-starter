package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

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
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetReminderById() = runBlockingTest {
        val reminder = ReminderDTO("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
        database.reminderDao().saveReminder(reminder)
        
        val dbReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat(dbReminder?.title, `is`(reminder.title))
        assertThat(dbReminder?.description, `is`(reminder.description))
        assertThat(dbReminder?.location, `is`(reminder.location))
        assertThat(dbReminder?.longitude, `is`(reminder.longitude))
        assertThat(dbReminder?.latitude, `is`(reminder.latitude))
    }

    @Test
    fun replaceExistingReminder() = runBlockingTest {
        val reminder1 = ReminderDTO("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
        database.reminderDao().saveReminder(reminder1)


        val reminder2 = ReminderDTO("Reminder 2", "Description 2", "Location 2", 48.2, 50.2, "reminder1")
        database.reminderDao().saveReminder(reminder2)

        val dbReminder = database.reminderDao().getReminderById(reminder1.id)

        assertThat(dbReminder?.title, `is`(reminder2.title))
        assertThat(dbReminder?.description, `is`(reminder2.description))
        assertThat(dbReminder?.location, `is`(reminder2.location))
        assertThat(dbReminder?.longitude, `is`(reminder2.longitude))
        assertThat(dbReminder?.latitude, `is`(reminder2.latitude))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        val reminder1 = ReminderDTO("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
        database.reminderDao().saveReminder(reminder1)

        database.reminderDao().deleteAllReminders()

        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.size, `is`(0))
    }

    @Test
    fun getAllReminders() = runBlockingTest {
        val reminder1 = ReminderDTO("Reminder 1", "Description 1", "Location 1", 48.1, 50.1, "reminder1")
        val reminder2 = ReminderDTO("Reminder 2", "Description 2", "Location 2", 48.2, 50.2, "reminder2")

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        val reminders = database.reminderDao().getReminders()
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