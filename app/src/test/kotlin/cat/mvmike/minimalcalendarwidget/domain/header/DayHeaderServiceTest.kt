// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.header

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.time.DayOfWeek
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

internal class DayHeaderServiceTest : BaseTest() {
    private val widgetRv = Mockito.mock(RemoteViews::class.java)
    private val headerRowRv = Mockito.mock(RemoteViews::class.java)

    @ParameterizedTest
    @MethodSource("combinationOfStartWeekDayAndThemeConfig")
    fun setDayHeaders_shouldAddViewBasedOnCurrentDayAndConfig(startWeekDay: DayOfWeek, theme: Theme) {
        mockStartWeekDay(sharedPreferences, startWeekDay)
        mockTheme(sharedPreferences, theme)
        Mockito.`when`(systemResolver.createHeaderRow(context)).thenReturn(headerRowRv)
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.MONDAY)).thenReturn("MON")
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.TUESDAY)).thenReturn("TUE")
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.WEDNESDAY)).thenReturn("WED")
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.THURSDAY)).thenReturn("THU")
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.FRIDAY)).thenReturn("FRI")
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SATURDAY)).thenReturn("SAT")
        Mockito.`when`(systemResolver.getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SUNDAY)).thenReturn("SUN")
        DayHeaderService.setDayHeaders(context, widgetRv)
        Mockito.verify(systemResolver, Mockito.times(1)).createHeaderRow(context)
        val inOrder = Mockito.inOrder(systemResolver)
        rotateWeekDays(startWeekDay.ordinal, theme)
                .forEach(Consumer { c: MutableMap.MutableEntry<String, Int> -> inOrder.verify(systemResolver, Mockito.times(1)).addHeaderDayToHeader(context, headerRowRv, c.key, c.value) })
        Mockito.verify(systemResolver, Mockito.times(1)).addHeaderRowToWidget(widgetRv, headerRowRv)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.MONDAY)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.TUESDAY)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.WEDNESDAY)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.THURSDAY)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.FRIDAY)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SATURDAY)
        Mockito.verify(systemResolver, Mockito.times(1)).getAbbreviatedDayOfWeekTranslated(context, DayOfWeek.SUNDAY)
        Mockito.verifyNoMoreInteractions(systemResolver)
    }

        private fun combinationOfStartWeekDayAndThemeConfig(): Stream<Arguments> {
            return Stream.concat(
                    Stream.of(*DayOfWeek.values()).map { dayOfWeek: DayOfWeek -> Arguments.of(dayOfWeek, Theme.BLACK) },
                    Stream.concat(
                            Stream.of(*DayOfWeek.values()).map { dayOfWeek: DayOfWeek -> Arguments.of(dayOfWeek, Theme.GREY) },
                            Stream.of(*DayOfWeek.values()).map { dayOfWeek: DayOfWeek -> Arguments.of(dayOfWeek, Theme.WHITE) })
            )


        private fun rotateWeekDays(numberOfPositions: Int, theme: Theme): Stream<MutableMap.MutableEntry<String, Int>> {
            val weekdays = arrayOf<String>("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            val result = arrayOfNulls<String>(weekdays.size)
            for (i in weekdays.indices) {
                result[(i + (weekdays.size - numberOfPositions)) % weekdays.size] = weekdays[i]
            }
            return Arrays.stream(result)
                    .map { c: String -> getWeekDayWithTheme(c, theme) }
        }

        private fun getWeekDayWithTheme(weekday: String, theme: Theme): MutableMap.MutableEntry<String, Int> {
            val cellHeaderThemeId: Int
            cellHeaderThemeId = when (weekday) {
                "SAT" -> theme.getCellHeaderSaturday()
                "SUN" -> theme.getCellHeaderSunday()
                else -> theme.getCellHeader()
            }
            return AbstractMap.SimpleEntry(weekday, cellHeaderThemeId)
        }
    }
}