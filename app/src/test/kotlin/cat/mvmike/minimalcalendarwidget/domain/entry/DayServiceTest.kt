// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.entry

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.HashSet
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet

internal class DayServiceTest : BaseTest() {
    private val widgetRv = Mockito.mock(RemoteViews::class.java)
    private val rowRv = Mockito.mock(RemoteViews::class.java)
    private val cellRv = Mockito.mock(RemoteViews::class.java)

    @Test
    fun setDays_shouldReturnSafeDateSpanOfSystemTimeZoneInstances() {
        Mockito.reset(widgetRv)
        BaseTest.Companion.mockStartWeekDay(sharedPreferences, DayOfWeek.MONDAY)
        BaseTest.Companion.mockTheme(sharedPreferences, Theme.BLACK)
        BaseTest.Companion.mockInstancesSymbols(sharedPreferences, Symbol.MINIMAL)
        BaseTest.Companion.mockInstancesSymbolsColour(sharedPreferences, Colour.CYAN)
        Mockito.`when`(systemResolver.systemLocalDate).thenReturn(LocalDate.of(2018, 12, 4))
        Mockito.`when`(systemResolver.isReadCalendarPermitted(context)).thenReturn(true)
        Mockito.`when`(systemResolver.createDay(ArgumentMatchers.any(Context::class.java), ArgumentMatchers.anyInt())).thenReturn(cellRv)
        Mockito.`when`(systemResolver.getColorInstancesTodayId(context)).thenReturn(98) // today
        Mockito.`when`(systemResolver.getColorInstancesId(context, Colour.CYAN)).thenReturn(99) // not today
        Mockito.`when`(systemResolver.createRow(context)).thenReturn(rowRv)
        DayService.setDays(context, widgetRv, getSpreadInstances())

        // per instance
        Mockito.verify(systemResolver, Mockito.times(1)).systemLocalDate

        // per week
        Mockito.verify(systemResolver, Mockito.times(6)).createRow(context)
        Mockito.verify(systemResolver, Mockito.times(6)).addRowToWidget(widgetRv, rowRv)
        Mockito.verify(systemResolver, Mockito.times(11)).createDay(context, 2131361820) // out of month
        Mockito.verify(systemResolver, Mockito.times(1)).createDay(context, 2131361826) // today
        Mockito.verify(systemResolver, Mockito.times(20)).createDay(context, 2131361825) // weekday
        Mockito.verify(systemResolver, Mockito.times(5)).createDay(context, 2131361821) // saturday
        Mockito.verify(systemResolver, Mockito.times(5)).createDay(context, 2131361823) // sunday
        Mockito.verify(systemResolver, Mockito.times(1)).getColorInstancesTodayId(context) // today
        Mockito.verify(systemResolver, Mockito.times(41)).getColorInstancesId(context, Colour.CYAN) // not today
        val inOrder = Mockito.inOrder(systemResolver)
        getExpectedDays().forEach(
                Consumer { c: MutableMap.MutableEntry<String?, Boolean?>? ->
                    inOrder.verify(systemResolver, Mockito.times(1))
                            .addDayCellRemoteView(context, rowRv, cellRv, c.key, c.value, c.key.startsWith(" 0"), 1.2f, if (c.value) 98 else 99)
                }
        )
        Mockito.verifyNoMoreInteractions(systemResolver)
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfThemesAndDayStatuses")
    fun getDayLayout_shouldComputeBasedOnThemeAndDayStatus(theme: Theme?, ds: Day?, expectedResult: Int) {
        Assertions.assertEquals(expectedResult, DayService.getDayLayout(theme, ds))
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfInstanceSetsAndDayStatuses")
    fun getNumberOfInstances_shouldComputeBasedOnInstanceSet(instanceSet: MutableSet<Instance?>?, ds: Day?, expectedResult: Int) {
        Assertions.assertEquals(expectedResult, DayService.getNumberOfInstances(instanceSet, ds))
    }

    @ParameterizedTest
    @MethodSource("getCombinationOfLocalDatesAndInitialLocalDate")
    fun getInitialLocalDate_shouldGetInitialLocalDate(systemLocalDate: LocalDate?, dayOfWeek: DayOfWeek?, expectedInitialLocalDate: LocalDate?) {
        Assertions.assertEquals(expectedInitialLocalDate, DayService.getInitialLocalDate(systemLocalDate, dayOfWeek.ordinal))
    }

    companion object {
        private val TODAY_WEEKDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 12, 4))
        private val TODAY_SATURDAY: Day? = Day(
                LocalDate.of(2018, 12, 8),
                LocalDate.of(2018, 12, 8))
        private val TODAY_SUNDAY: Day? = Day(
                LocalDate.of(2018, 12, 9),
                LocalDate.of(2018, 12, 9))
        private val IN_MONTH_WEEKDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 12, 14))
        private val IN_MONTH_SATURDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 12, 15))
        private val IN_MONTH_SUNDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 12, 16))
        private val NOT_IN_MONTH_WEEKDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 11, 23))
        private val NOT_IN_MONTH_SATURDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 11, 24))
        private val NOT_IN_MONTH_SUNDAY: Day? = Day(
                LocalDate.of(2018, 12, 4),
                LocalDate.of(2018, 11, 25))

        private fun getSpreadInstances(): MutableSet<Instance?>? {
            return Stream.of(
                    Instance(1543190400000L, 1543276800000L, 0, 0),  // 11/26 all day
                    Instance(1543363200000L, 1543449600000L, 0, 0),  // 11/28 all day
                    Instance(1543449600000L, 1543536000000L, 0, 0),  // 11/29 all day
                    Instance(1543795200000L, 1543967999000L, 0, 0),  // 12/3 all day
                    Instance(1543881600000L, 1543968000000L, 0, 0),  // 12/4 all day
                    Instance(1544054400000L, 1544140800000L, 0, 0),  // 12/6 all day
                    Instance(1544054400000L, 1544140800000L, 0, 0),  // 12/6 all day
                    Instance(1544054400000L, 1544140800000L, 0, 0),  // 12/6 all day
                    Instance(1544400000000L, 1544486400000L, 0, 0),  // 12/10 all day
                    Instance(1544400000000L, 1544486400000L, 0, 0),  // 12/10 all day
                    Instance(1544400000000L, 1544486400000L, 0, 0),  // 12/10 all day
                    Instance(1544400000000L, 1544486400000L, 0, 0),  // 12/10 all day
                    Instance(1545868800000L, 1545955200000L, 0, 0),  // 12/27 all day
                    Instance(1546128000000L, 1546214400000L, 0, 0),  // 12/30 all day
                    Instance(1546128000000L, 1546214400000L, 0, 0),  // 12/30 all day
                    Instance(1546128000000L, 1546214400000L, 0, 0),  // 12/30 all day
                    Instance(1546128000000L, 1546214400000L, 0, 0),  // 12/30 all day
                    Instance(1546128000000L, 1546214400000L, 0, 0),  // 12/30 all day
                    Instance(1546300800000L, 1546387200000L, 0, 0),  // 01/01 all day
                    Instance(1546387200000L, 1546473600000L, 0, 0),  // 01/02 all day
                    Instance(1546646400000L, 1546732800000L, 0, 0),  // 01/05 all day
                    Instance(1546646400000L, 1546732800000L, 0, 0),  // 01/05 all day
                    Instance(1546646400000L, 1546732800000L, 0, 0),  // 01/05 all day
                    Instance(1546646400000L, 1546732800000L, 0, 0),  // 01/05 all day
                    Instance(1546646400000L, 1546732800000L, 0, 0),  // 01/05 all day
                    Instance(1546646400000L, 1546732800000L, 0, 0) // 01/05 all day
            ).collect(Collectors.toCollection { HashSet() })
        }

        private fun getExpectedDays(): Stream<MutableMap.MutableEntry<String?, Boolean?>?>? {
            return Stream.of(
                    AbstractMap.SimpleEntry(" 26 ·", false),
                    AbstractMap.SimpleEntry(" 27  ", false),
                    AbstractMap.SimpleEntry(" 28 ·", false),
                    AbstractMap.SimpleEntry(" 29 ·", false),
                    AbstractMap.SimpleEntry(" 30  ", false),
                    AbstractMap.SimpleEntry(" 01  ", false),
                    AbstractMap.SimpleEntry(" 02  ", false),
                    AbstractMap.SimpleEntry(" 03 ·", false),
                    AbstractMap.SimpleEntry(" 04 ∶", true),
                    AbstractMap.SimpleEntry(" 05 ·", false),
                    AbstractMap.SimpleEntry(" 06 ∴", false),
                    AbstractMap.SimpleEntry(" 07  ", false),
                    AbstractMap.SimpleEntry(" 08  ", false),
                    AbstractMap.SimpleEntry(" 09  ", false),
                    AbstractMap.SimpleEntry(" 10 ∷", false),
                    AbstractMap.SimpleEntry(" 11  ", false),
                    AbstractMap.SimpleEntry(" 12  ", false),
                    AbstractMap.SimpleEntry(" 13  ", false),
                    AbstractMap.SimpleEntry(" 14  ", false),
                    AbstractMap.SimpleEntry(" 15  ", false),
                    AbstractMap.SimpleEntry(" 16  ", false),
                    AbstractMap.SimpleEntry(" 17  ", false),
                    AbstractMap.SimpleEntry(" 18  ", false),
                    AbstractMap.SimpleEntry(" 19  ", false),
                    AbstractMap.SimpleEntry(" 20  ", false),
                    AbstractMap.SimpleEntry(" 21  ", false),
                    AbstractMap.SimpleEntry(" 22  ", false),
                    AbstractMap.SimpleEntry(" 23  ", false),
                    AbstractMap.SimpleEntry(" 24  ", false),
                    AbstractMap.SimpleEntry(" 25  ", false),
                    AbstractMap.SimpleEntry(" 26  ", false),
                    AbstractMap.SimpleEntry(" 27 ·", false),
                    AbstractMap.SimpleEntry(" 28  ", false),
                    AbstractMap.SimpleEntry(" 29  ", false),
                    AbstractMap.SimpleEntry(" 30 ◇", false),
                    AbstractMap.SimpleEntry(" 31  ", false),
                    AbstractMap.SimpleEntry(" 01 ·", false),
                    AbstractMap.SimpleEntry(" 02 ·", false),
                    AbstractMap.SimpleEntry(" 03  ", false),
                    AbstractMap.SimpleEntry(" 04  ", false),
                    AbstractMap.SimpleEntry(" 05 ◈", false),
                    AbstractMap.SimpleEntry(" 06  ", false)
            )
        }

        private fun getCombinationOfThemesAndDayStatuses(): Stream<Arguments?>? {
            return Stream.of(
                    Arguments.of(Theme.BLACK, TODAY_WEEKDAY, 2131361826),
                    Arguments.of(Theme.BLACK, TODAY_SATURDAY, 2131361822),
                    Arguments.of(Theme.BLACK, TODAY_SUNDAY, 2131361824),
                    Arguments.of(Theme.BLACK, IN_MONTH_WEEKDAY, 2131361825),
                    Arguments.of(Theme.BLACK, IN_MONTH_SATURDAY, 2131361821),
                    Arguments.of(Theme.BLACK, IN_MONTH_SUNDAY, 2131361823),
                    Arguments.of(Theme.BLACK, NOT_IN_MONTH_WEEKDAY, 2131361820),
                    Arguments.of(Theme.BLACK, NOT_IN_MONTH_SATURDAY, 2131361820),
                    Arguments.of(Theme.BLACK, NOT_IN_MONTH_SUNDAY, 2131361820),
                    Arguments.of(Theme.GREY, TODAY_WEEKDAY, 2131361838),
                    Arguments.of(Theme.GREY, TODAY_SATURDAY, 2131361834),
                    Arguments.of(Theme.GREY, TODAY_SUNDAY, 2131361836),
                    Arguments.of(Theme.GREY, IN_MONTH_WEEKDAY, 2131361837),
                    Arguments.of(Theme.GREY, IN_MONTH_SATURDAY, 2131361833),
                    Arguments.of(Theme.GREY, IN_MONTH_SUNDAY, 2131361835),
                    Arguments.of(Theme.GREY, NOT_IN_MONTH_WEEKDAY, 2131361832),
                    Arguments.of(Theme.GREY, NOT_IN_MONTH_SATURDAY, 2131361832),
                    Arguments.of(Theme.GREY, NOT_IN_MONTH_SUNDAY, 2131361832),
                    Arguments.of(Theme.WHITE, TODAY_WEEKDAY, 2131361861),
                    Arguments.of(Theme.WHITE, TODAY_SATURDAY, 2131361857),
                    Arguments.of(Theme.WHITE, TODAY_SUNDAY, 2131361859),
                    Arguments.of(Theme.WHITE, IN_MONTH_WEEKDAY, 2131361860),
                    Arguments.of(Theme.WHITE, IN_MONTH_SATURDAY, 2131361856),
                    Arguments.of(Theme.WHITE, IN_MONTH_SUNDAY, 2131361858),
                    Arguments.of(Theme.WHITE, NOT_IN_MONTH_WEEKDAY, 2131361855),
                    Arguments.of(Theme.WHITE, NOT_IN_MONTH_SATURDAY, 2131361855),
                    Arguments.of(Theme.WHITE, NOT_IN_MONTH_SUNDAY, 2131361855)
            )
        }

        private fun getCombinationOfInstanceSetsAndDayStatuses(): Stream<Arguments?>? {
            return Stream.of( // all in
                    Arguments.of(Stream.of(
                            Instance(1543881600000L, 1543967999000L, 0, 0),  // 12/4 00:00 - 12/5 00:00
                            Instance(1543950000000L, 1543957200000L, 0, 0) // 12/4 22:00 - 12/5 00:00
                    ).collect(Collectors.toCollection { HashSet() }), TODAY_WEEKDAY, 2),  // with instances that are in more than 1 day
                    Arguments.of(Stream.of(
                            Instance(1543795200000L, 1543967999000L, 0, 1),  // 12/3 00:00 - 12/5 00:00
                            Instance(1543881600000L, 1544054400000L, 0, 1),  // 12/4 00:00 - 12/6 00:00
                            Instance(1543863600000L, 1543874400000L, 0, 0),  // 12/3 22:00 - 12/4 01:00
                            Instance(1543870800000L, 1543993200000L, 0, 1) // 12/4 00:00 - 12/5 10:00
                    ).collect(Collectors.toCollection { HashSet() }), TODAY_WEEKDAY, 4),  // with instances before and after the day
                    Arguments.of(Stream.of(
                            Instance(1543795200000L, 1543881600000L, 0, 0),  // 12/3 00:00 - 12/4 00:00
                            Instance(1543968000000L, 1544054400000L, 0, 0),  // 12/5 00:00 - 12/6 00:00
                            Instance(1543863600000L, 1543870800000L, 0, 1),  // 12/3 22:00 - 12/4 00:00
                            Instance(1543957200000L, 1543993200000L, 0, 0) // 12/5 00:00 - 12/5 10:00
                    ).collect(Collectors.toCollection { HashSet() }), TODAY_WEEKDAY, 0)
            )
        }

        private fun getCombinationOfLocalDatesAndInitialLocalDate(): Stream<Arguments?>? {
            return Stream.of(
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.MONDAY,
                            LocalDate.of(2019, 12, 30)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.TUESDAY,
                            LocalDate.of(2019, 12, 31)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.WEDNESDAY,
                            LocalDate.of(2020, 1, 1)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.THURSDAY,
                            LocalDate.of(2019, 12, 26)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.FRIDAY,
                            LocalDate.of(2019, 12, 27)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.SATURDAY,
                            LocalDate.of(2019, 12, 28)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 20), DayOfWeek.SUNDAY,
                            LocalDate.of(2019, 12, 29)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 1), DayOfWeek.MONDAY,
                            LocalDate.of(2019, 12, 30)),
                    Arguments.of(
                            LocalDate.of(2020, 1, 31), DayOfWeek.MONDAY,
                            LocalDate.of(2019, 12, 30))
            )
        }
    }
}