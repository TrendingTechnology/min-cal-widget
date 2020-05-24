// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.header

import android.content.Context
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService
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
import java.time.DayOfWeek

object DayHeaderService {
    fun setDayHeaders(context: Context?, widgetRv: RemoteViews?) {
        val headerRowRv: RemoteViews = SystemResolver.Companion.get().createHeaderRow(context)
        val firstDayOfWeek = ConfigurationService.getStartWeekDay(context).ordinal
        val theme = ConfigurationService.getTheme(context)
        for (dayOfWeek in DayOfWeek.values()) {
            val currentOrdinal = dayOfWeek.ordinal
            val newOrdinal = if (firstDayOfWeek + currentOrdinal < DayOfWeek.values().size) firstDayOfWeek + currentOrdinal else (firstDayOfWeek + currentOrdinal) % DayOfWeek.values().size
            val current = DayOfWeek.values()[newOrdinal]
            var cellHeaderThemeId: Int
            cellHeaderThemeId = when (current) {
                DayOfWeek.SATURDAY -> theme.cellHeaderSaturday
                DayOfWeek.SUNDAY -> theme.cellHeaderSunday
                else -> theme.cellHeader
            }
            SystemResolver.Companion.get().addHeaderDayToHeader(context, headerRowRv,
                    SystemResolver.Companion.get().getAbbreviatedDayOfWeekTranslated(context, current), cellHeaderThemeId)
        }
        SystemResolver.Companion.get().addHeaderRowToWidget(widgetRv, headerRowRv)
    }
}