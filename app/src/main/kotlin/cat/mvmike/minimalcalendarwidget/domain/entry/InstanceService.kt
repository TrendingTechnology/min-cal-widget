// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.content.Context
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object InstanceService {
    private const val CALENDAR_DAYS_SPAN = 45
    fun getInstancesWithTimeout(context: Context, timeout: Long, timeUnit: TimeUnit): MutableSet<Instance> {
        return if (!SystemResolver.Companion.get().isReadCalendarPermitted(context)) {
            HashSet<Instance>()
        } else try {
            CompletableFuture.supplyAsync { readAllInstances(context) }[timeout, timeUnit]
        } catch (ignored: Exception) {
            HashSet<Instance>()
        }
    }

    fun readAllInstances(context: Context): MutableSet<Instance> {
        val current: LocalDate = SystemResolver.Companion.get().getSystemLocalDate()
        return SystemResolver.Companion.get().getInstances(
                context,
                toStartOfDayInEpochMilli(current.minus(CALENDAR_DAYS_SPAN.toLong(), ChronoUnit.DAYS)),
                toStartOfDayInEpochMilli(current.plus(CALENDAR_DAYS_SPAN.toLong(), ChronoUnit.DAYS))
        )
    }

    fun toStartOfDayInEpochMilli(localDate: LocalDate): Long {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}