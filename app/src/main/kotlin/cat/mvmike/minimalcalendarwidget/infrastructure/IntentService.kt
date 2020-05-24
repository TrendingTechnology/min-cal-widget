// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.application.MonthWidget
import cat.mvmike.minimalcalendarwidget.application.activity.CalendarActivity
import cat.mvmike.minimalcalendarwidget.application.activity.ConfigurationActivity
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

object IntentService {
    private const val INTENT_CODE_CONFIGURATION = 98
    private const val INTENT_CODE_CALENDAR = 99
    private val WIDGET_PRESS: String? = "action.WIDGET_PRESS"
    private val CONFIGURATION_PRESS: String? = "action.WIDGET_CONFIGURATION"
    fun addListeners(context: Context?, widgetRemoteView: RemoteViews?) {

        // for all widget → open calendar
        widgetRemoteView.setOnClickPendingIntent(R.id.calendar_widget, PendingIntent.getBroadcast(context, INTENT_CODE_CALENDAR,
                Intent(context, MonthWidget::class.java).setAction(WIDGET_PRESS), PendingIntent.FLAG_UPDATE_CURRENT))

        // for configuration icon → open config
        widgetRemoteView.setOnClickPendingIntent(R.id.configuration_icon, PendingIntent.getBroadcast(context, INTENT_CODE_CONFIGURATION,
                Intent(context, MonthWidget::class.java).setAction(CONFIGURATION_PRESS), PendingIntent.FLAG_UPDATE_CURRENT))
    }

    fun processIntent(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                WIDGET_PRESS -> CalendarActivity.start(context)
                CONFIGURATION_PRESS -> ConfigurationActivity.Companion.start(context)
            }
        }
    }
}