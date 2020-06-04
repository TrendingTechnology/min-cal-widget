// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.header

import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.time.Instant
import java.util.*
import java.util.stream.Stream

internal class MonthYearHeaderServiceTest : BaseTest() {
    private val widgetRv = Mockito.mock(RemoteViews::class.java)

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInEnglish")
    fun setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInEnglish(instant: Instant, expectedMonthAndYear: String) {
        Mockito.`when`(systemResolver.getInstant()).thenReturn(instant)
        Mockito.`when`(systemResolver.getLocale(context)).thenReturn(Locale.ENGLISH)
        MonthYearHeaderService.setMonthYearHeader(context, widgetRv)
        Mockito.verify(systemResolver, Mockito.times(1)).getInstant()
        Mockito.verify(systemResolver, Mockito.times(1)).getLocale(context)
        Mockito.verify(systemResolver, Mockito.times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f)
        Mockito.verifyNoMoreInteractions(systemResolver)
    }

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInCatalan")
    fun setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInCatalan(instant: Instant, expectedMonthAndYear: String) {
        Mockito.`when`(systemResolver.getInstant()).thenReturn(instant)
        Mockito.`when`(systemResolver.getLocale(context)).thenReturn(Locale("ca", "ES"))
        MonthYearHeaderService.setMonthYearHeader(context, widgetRv)
        Mockito.verify(systemResolver, Mockito.times(1)).getInstant()
        Mockito.verify(systemResolver, Mockito.times(1)).getLocale(context)
        Mockito.verify(systemResolver, Mockito.times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f)
        Mockito.verifyNoMoreInteractions(systemResolver)
    }

    @ParameterizedTest
    @MethodSource("getSpreadInstantsInRussian")
    fun setMonthYearHeader_shouldAddViewBasedOnCurrentMonthAndYearInRussian(instant: Instant, expectedMonthAndYear: String) {
        Mockito.`when`(systemResolver.getInstant()).thenReturn(instant)
        Mockito.`when`(systemResolver.getLocale(context)).thenReturn(Locale("ru", "RU"))
        MonthYearHeaderService.setMonthYearHeader(context, widgetRv)
        Mockito.verify(systemResolver, Mockito.times(1)).getInstant()
        Mockito.verify(systemResolver, Mockito.times(1)).getLocale(context)
        Mockito.verify(systemResolver, Mockito.times(1)).createMonthYearHeader(widgetRv, expectedMonthAndYear, 0.7f)
        Mockito.verifyNoMoreInteractions(systemResolver)
    }

        private fun getSpreadInstantsInEnglish(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(Instant.ofEpochMilli(896745600000L), "June 1998"),  // 1998-06-02 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1516924800000L), "January 2018"),  // 2018-01-26 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1804204800000L), "March 2027"),  // 2027-03-05 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1108771200000L), "February 2005"),  // 2005-02-19 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1544659200000L), "December 2018") // 2018-12-13 00:00 UTC
            )
        }

        private fun getSpreadInstantsInCatalan(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(Instant.ofEpochMilli(896745600000L), "Juny 1998"),  // 1998-06-02 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1516924800000L), "Gener 2018"),  // 2018-01-26 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1804204800000L), "Març 2027"),  // 2027-03-05 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1108771200000L), "Febrer 2005"),  // 2005-02-19 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1544659200000L), "Desembre 2018") // 2018-12-13 00:00 UTC
            )
        }

        private fun getSpreadInstantsInRussian(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(Instant.ofEpochMilli(896745600000L), "Июнь 1998"),  // 1998-06-02 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1516924800000L), "Январь 2018"),  // 2018-01-26 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1804204800000L), "Март 2027"),  // 2027-03-05 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1108771200000L), "Февраль 2005"),  // 2005-02-19 00:00 UTC
                    Arguments.of(Instant.ofEpochMilli(1544659200000L), "Декабрь 2018") // 2018-12-13 00:00 UTC
            )
        }

}