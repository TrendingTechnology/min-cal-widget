// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.provider.CalendarContract
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
import java.time.Instant

class Instance(epochMilliStart: Long, epochMilliEnd: Long, julianStartDay: Int, julianEndDate: Int) {
    private val start: Instant?
    private val end: Instant?
    private val allDay: Boolean
    fun getStart(): Instant? {
        return start
    }

    fun getEnd(): Instant? {
        return end
    }

    fun isAllDay(): Boolean {
        return allDay
    }

    companion object {
        val FIELDS: Array<String?>? = arrayOf(
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.START_DAY,
                CalendarContract.Instances.END_DAY
        )
        private const val MILLIS_IN_A_DAY = 86400000
        private fun computeAllDay(start: Instant?, end: Instant?, julianStartDay: Int, julianEndDate: Int): Boolean {
            return ((end.toEpochMilli() - start.toEpochMilli()) % MILLIS_IN_A_DAY == 0L
                    && (end.toEpochMilli() - start.toEpochMilli()) / MILLIS_IN_A_DAY == julianEndDate - julianStartDay + 1.toLong())
        }
    }

    init {
        start = Instant.ofEpochMilli(epochMilliStart)
        end = Instant.ofEpochMilli(epochMilliEnd)
        allDay = computeAllDay(start, end, julianStartDay, julianEndDate)
    }
}