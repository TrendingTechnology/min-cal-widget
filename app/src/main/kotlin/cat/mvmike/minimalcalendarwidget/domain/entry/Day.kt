// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

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
import java.text.DecimalFormat
import java.time.*

class Day(private val systemLocalDate: LocalDate?, private val localDate: LocalDate?) {
    fun inYear(): Boolean {
        return localDate.getYear() == systemLocalDate.getYear()
    }

    fun inMonth(): Boolean {
        return inYear() && localDate.getMonth() == systemLocalDate.getMonth()
    }

    fun isToday(): Boolean {
        return inMonth() && localDate.getDayOfYear() == systemLocalDate.getDayOfYear()
    }

    fun getDayOfWeek(): DayOfWeek? {
        return localDate.getDayOfWeek()
    }

    fun getDayOfMonthString(): String? {
        return DecimalFormat(DAY_OF_MONTH_DF_PATTERN).format(localDate.getDayOfMonth().toLong())
    }

    fun isSingleDigitDay(): Boolean {
        return localDate.getDayOfMonth() < 10
    }

    fun isInDay(startInstant: Instant?, endInstant: Instant?, allDayInstance: Boolean): Boolean {

        // take out 5 milliseconds to avoid erratic behaviour with full day events (or those that end at 00:00)
        return toLocalDate(startInstant, allDayInstance).getMonthValue() <= localDate.getMonthValue() && toLocalDate(startInstant, allDayInstance).getDayOfMonth() <= localDate.getDayOfMonth() && toLocalDate(endInstant.minusMillis(5), allDayInstance).getMonthValue() >= localDate.getMonthValue() && toLocalDate(endInstant.minusMillis(5), allDayInstance).getDayOfMonth() >= localDate.getDayOfMonth()
    }

    companion object {
        private val DAY_OF_MONTH_DF_PATTERN: String? = "00"

        // calendarProvider uses different set of timezones depending if event is allDay
        private fun toLocalDate(instant: Instant?, allDayInstance: Boolean): LocalDateTime? {
            return if (allDayInstance) {
                LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
            } else LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        }
    }

}