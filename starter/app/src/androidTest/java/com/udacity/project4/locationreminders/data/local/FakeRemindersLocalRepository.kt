package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.util.LinkedHashMap

class FakeRemindersLocalRepository() : ReminderDataSource {

    var reminders: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (reminders.isEmpty()) return Result.Error("No Data")
        return Result.Success(reminders.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminderDto = reminders[id]
        if (reminderDto != null) {
            return Result.Success(reminderDto)
        }
        return Result.Error("Not found")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}