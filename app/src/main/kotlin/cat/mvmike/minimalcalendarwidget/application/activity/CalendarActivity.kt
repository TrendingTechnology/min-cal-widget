// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.activity

import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.ConfigurableItemTest
import cat.mvmike.minimalcalendarwidget.domain.entry.DayServiceTest
import cat.mvmike.minimalcalendarwidget.domain.header.DayHeaderServiceTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers
import org.mockito.InOrder
import org.mockito.Mockito

object CalendarActivity {
    private val NO_CALENDAR_APPLICATION_FOUND: String? = "No calendar application found"
    private val TIME_APPEND_PATH: String? = "time"
    fun start(context: Context?) {
        val startMillis = System.currentTimeMillis()
        val builder = CalendarContract.CONTENT_URI.buildUpon()
        builder.appendPath(TIME_APPEND_PATH)
        ContentUris.appendId(builder, startMillis)
        val calendarIntent = Intent(Intent.ACTION_VIEW).setData(builder.build())
        calendarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(calendarIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, NO_CALENDAR_APPLICATION_FOUND, Toast.LENGTH_SHORT).show()
        }
    }
}