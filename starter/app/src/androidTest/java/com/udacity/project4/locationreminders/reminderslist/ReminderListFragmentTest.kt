package com.udacity.project4.locationreminders.reminderslist

import android.content.ComponentName
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MyApp
import com.udacity.project4.R
import com.udacity.project4.base.DataBindingViewHolder
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeRemindersLocalRepository
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

//    TODO: add testing for the error messages.

    private lateinit var repository: ReminderDataSource

    @Before fun initialize() {
        stopKoin()

        val myModule = module {
            // define your module for test here
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { FakeRemindersLocalRepository() as ReminderDataSource }
            single { LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext()) }
        }
        startKoin {
            androidLogger()
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(myModule))
        }
        repository = getKoin().get()
        Intents.init()
    }

    @After
    @ExperimentalCoroutinesApi
    fun tearDown() = runBlockingTest {
        repository.deleteAllReminders()
        Intents.release()
    }

    @Test
    fun noDataMessage_isVisible() {
        // GIVEN - No data
        // WHEN - List fragment is launched
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // Then the no data massage is displayed
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigate_fromList_toDetail() = runBlockingTest {
        // GIVEN - Reminder is persisted in DB
        val reminder = ReminderDTO(
            "Reminder 1",
            "Description 1",
            "Location 1",
            48.1,
            50.1,
            "reminder1"
        )
        repository.saveReminder(reminder)

        // WHEN - List fragment is launched
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // and item is clicked
        onView(withId(R.id.reminderssRecyclerView)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText(reminder.title)), ViewActions.click()
            )
        )

        val app = ApplicationProvider.getApplicationContext() as MyApp

        intended(hasComponent(ReminderDescriptionActivity::class.java.name))
        onView(withId(R.id.reminder_title)).check(matches(withText(reminder.title)))
        onView(withId(R.id.reminder_description)).check(matches(withText(reminder.description)))
        onView(withId(R.id.reminder_location)).check(matches(withText(reminder.location)))
        onView(withId(R.id.reminder_lng_lat))
            .check(
                matches(
                    withText(
                        app.getString(
                            R.string.lat_long_snippet,
                            reminder.latitude,
                            reminder.longitude
                        )
                    )
                )
            )
    }

    @Test
    @ExperimentalCoroutinesApi
    fun reminder_displayedInUi() = runBlockingTest {
        // GIVEN - Reminder is persisted in DB
        val reminder = ReminderDTO(
            "Reminder 1",
            "Description 1",
            "Location 1",
            48.1,
            50.1,
            "reminder1"
        )

        repository.saveReminder(reminder)

        // WHEN - List fragment is launched
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // Then the reminders title, description and location properties are visible
        onView(withText(reminder.title)).check(matches(isDisplayed()))
        onView(withText(reminder.description)).check(matches(isDisplayed()))
        onView(withText(reminder.location)).check(matches(isDisplayed()))
    }
}