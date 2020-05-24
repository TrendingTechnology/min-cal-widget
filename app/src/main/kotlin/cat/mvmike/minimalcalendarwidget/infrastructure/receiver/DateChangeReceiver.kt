// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.MonthWidget
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
import java.time.LocalDateTime
import java.time.temporal.ChronoField

class DateChangeReceiver : BroadcastReceiver() {
    private var lastChecked: LocalDateTime? = SystemResolver.Companion.get().getSystemLocalDateTime()
    override fun onReceive(context: Context?, intent: Intent?) {
        val now: LocalDateTime = SystemResolver.Companion.get().getSystemLocalDateTime()
        if (now[ChronoField.YEAR] != lastChecked.get(ChronoField.YEAR)
                || now[ChronoField.DAY_OF_YEAR] != lastChecked.get(ChronoField.DAY_OF_YEAR)) {
            MonthWidget.Companion.forceRedraw(context)
        }
        lastChecked = now
    }
}