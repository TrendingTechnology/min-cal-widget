// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.stream.IntStream

object DayService {
    private val PADDING: String = " "
    private const val MONTH_FIRST_DAY = 1
    private const val MAXIMUM_DAYS_IN_MONTH = 31
    private const val NUM_WEEKS = 6
    private const val DAYS_IN_WEEK = 7
    fun setDays(context: Context, remoteViews: RemoteViews, instanceSet: MutableSet<Instance>) {
        val firstDayOfWeek = ConfigurationService.getStartWeekDay(context).ordinal
        val theme = ConfigurationService.getTheme(context)
        val symbol = ConfigurationService.getInstancesSymbols(context)
        val colour = ConfigurationService.getInstancesSymbolsColours(context)
        val systemLocalDate: LocalDate = SystemResolver.Companion.get().getSystemLocalDate()
        IntStream.range(0, NUM_WEEKS).forEach { week: Int ->
            val rowRv: RemoteViews = SystemResolver.Companion.get().createRow(context)
            IntStream.range(0, DAYS_IN_WEEK).forEach { day: Int ->
                val localDate = getInitialLocalDate(systemLocalDate, firstDayOfWeek).plus(week * DAYS_IN_WEEK + day.toLong(), ChronoUnit.DAYS)
                val currentDay = Day(systemLocalDate, localDate)
                val cellRv: RemoteViews = SystemResolver.Companion.get().createDay(context, getDayLayout(theme, currentDay))
                val numberOfInstances = getNumberOfInstances(instanceSet, currentDay)
                val color: Int = if (currentDay.isToday()) SystemResolver.Companion.get().getColorInstancesTodayId(context) else SystemResolver.Companion.get().getColorInstancesId(context, colour)
                SystemResolver.Companion.get().addDayCellRemoteView(
                        context, rowRv, cellRv,
                        PADDING + currentDay.getDayOfMonthString() + PADDING + symbol.getSymbol(numberOfInstances),
                        currentDay.isToday(), currentDay.isSingleDigitDay(), symbol.getRelativeSize(), color)
            }
            SystemResolver.Companion.get().addRowToWidget(remoteViews, rowRv)
        }
    }

    fun getDayLayout(theme: Theme, ds: Day): Int {
        if (ds.isToday()) {
            return theme.getCellToday(ds.getDayOfWeek())
        }
        return if (ds.inMonth()) {
            theme.getCellThisMonth(ds.getDayOfWeek())
        } else theme.getCellNotThisMonth()
    }

    fun getNumberOfInstances(instanceSet: MutableSet<Instance>, ds: Day): Int {
        return if (instanceSet.isEmpty()) {
            0
        } else instanceSet.stream()
                .filter { instance: Instance -> ds.isInDay(instance.getStart(), instance.getEnd(), instance.isAllDay()) }
                .count().toInt()
    }

    fun getInitialLocalDate(systemLocalDate: LocalDate, firstDayOfWeek: Int): LocalDate {
        val firstDayOfMonth = LocalDate.of(systemLocalDate.getYear(), systemLocalDate.getMonthValue(), MONTH_FIRST_DAY)
        val difference = firstDayOfWeek - firstDayOfMonth[ChronoField.DAY_OF_WEEK] + 1
        val localDate = firstDayOfMonth.plus(difference.toLong(), ChronoUnit.DAYS)

        // overlap month manually if dayOfMonth is in current month and greater than 1
        return if (localDate[ChronoField.DAY_OF_MONTH] > MONTH_FIRST_DAY
                && localDate[ChronoField.DAY_OF_MONTH] < MAXIMUM_DAYS_IN_MONTH / 2) {
            localDate.minus(DAYS_IN_WEEK.toLong(), ChronoUnit.DAYS)
        } else localDate
    }
}