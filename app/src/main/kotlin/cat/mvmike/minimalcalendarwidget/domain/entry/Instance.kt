// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.provider.CalendarContract
import java.time.Instant

class Instance(epochMilliStart: Long,
               epochMilliEnd: Long,
               julianStartDay: Int,
               julianEndDate: Int) {

    private val MILLIS_IN_A_DAY = 86400000

    private val start: Instant
    private val end: Instant
    private val allDay: Boolean


    fun getStart(): Instant {
        return start
    }

    fun getEnd(): Instant {
        return end
    }

    fun isAllDay(): Boolean {
        return allDay
    }




    private val FIELDS: Array<String> = arrayOf(
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.START_DAY,
            CalendarContract.Instances.END_DAY
    )

    private fun computeAllDay(start: Instant, end: Instant, julianStartDay: Int, julianEndDate: Int): Boolean {
        return ((end.toEpochMilli() - start.toEpochMilli()) % MILLIS_IN_A_DAY == 0L
                && (end.toEpochMilli() - start.toEpochMilli()) / MILLIS_IN_A_DAY == julianEndDate - julianStartDay + 1.toLong())
    }

    init {
        start = Instant.ofEpochMilli(epochMilliStart)
        end = Instant.ofEpochMilli(epochMilliEnd)
        allDay = computeAllDay(start, end, julianStartDay, julianEndDate)
    }
}