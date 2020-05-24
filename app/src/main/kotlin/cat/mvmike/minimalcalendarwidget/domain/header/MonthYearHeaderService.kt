// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.header

import android.content.Context
import android.widget.RemoteViews
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object MonthYearHeaderService {
    private val MONTH_FORMAT_NON_STANDALONE: String? = "MMMM"
    private val MONTH_FORMAT_STANDALONE: String? = "LLLL"

    // need to differ special standalone locales because of https://bugs.openjdk.java.net/browse/JDK-8114833
    private val LANGUAGES_WITH_STANDALONE_CASE: MutableSet<String?>? = HashSet(listOf<String?>("ru"))
    private val YEAR_FORMAT: String? = "yyyy"
    private const val HEADER_RELATIVE_YEAR_SIZE = 0.7f
    fun setMonthYearHeader(context: Context?, widgetRemoteView: RemoteViews?) {
        val locale: Locale = SystemResolver.Companion.get().getLocale(context)
        val systemInstant: Instant = SystemResolver.Companion.get().getInstant()
        val displayMonth = getMonthDisplayValue(getMonthFormatter(locale).format(systemInstant), locale)
        val displayYear = getYearFormatter(locale).format(systemInstant)
        SystemResolver.Companion.get().createMonthYearHeader(widgetRemoteView, "$displayMonth $displayYear", HEADER_RELATIVE_YEAR_SIZE)
    }

    private fun getMonthFormatter(locale: Locale?): DateTimeFormatter? {
        return DateTimeFormatter
                .ofPattern(if (LANGUAGES_WITH_STANDALONE_CASE.contains(locale.getLanguage())) MONTH_FORMAT_STANDALONE else MONTH_FORMAT_NON_STANDALONE)
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
    }

    private fun getYearFormatter(locale: Locale?): DateTimeFormatter? {
        return DateTimeFormatter
                .ofPattern(YEAR_FORMAT)
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
    }

    private fun getMonthDisplayValue(month: String?, locale: Locale?): String? {
        val monthTokens: Array<String?> = month.split(" ").toTypedArray()
        val lastToken = monthTokens[monthTokens.size - 1]
        return (lastToken.substring(0, 1).toUpperCase(locale)
                + lastToken.substring(1).toLowerCase(locale))
    }
}