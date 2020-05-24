// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.content.Context
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.ConfigurableItemTest
import cat.mvmike.minimalcalendarwidget.domain.entry.DayServiceTest
import cat.mvmike.minimalcalendarwidget.domain.header.DayHeaderServiceTest
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers
import org.mockito.InOrder
import org.mockito.Mockito
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object InstanceService {
    private const val CALENDAR_DAYS_SPAN = 45
    fun getInstancesWithTimeout(context: Context?, timeout: Long, timeUnit: TimeUnit?): Optional<MutableSet<Instance?>?>? {
        return if (!SystemResolver.Companion.get().isReadCalendarPermitted(context)) {
            Optional.of(HashSet())
        } else try {
            Optional.of(CompletableFuture.supplyAsync { readAllInstances(context) }[timeout, timeUnit])
        } catch (ignored: ExecutionException) {
            Optional.empty()
        } catch (ignored: TimeoutException) {
            Optional.empty()
        } catch (ignored: InterruptedException) {
            Optional.empty()
        }
    }

    fun readAllInstances(context: Context?): MutableSet<Instance?>? {
        val current: LocalDate = SystemResolver.Companion.get().getSystemLocalDate()
        return SystemResolver.Companion.get().getInstances(
                context,
                toStartOfDayInEpochMilli(current.minus(CALENDAR_DAYS_SPAN.toLong(), ChronoUnit.DAYS)),
                toStartOfDayInEpochMilli(current.plus(CALENDAR_DAYS_SPAN.toLong(), ChronoUnit.DAYS))
        )
    }

    fun toStartOfDayInEpochMilli(localDate: LocalDate?): Long {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}