// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.ConfigurableItemTest
import cat.mvmike.minimalcalendarwidget.domain.entry.DayService
import cat.mvmike.minimalcalendarwidget.domain.entry.InstanceService
import cat.mvmike.minimalcalendarwidget.domain.header.DayHeaderService
import cat.mvmike.minimalcalendarwidget.domain.header.DayHeaderServiceTest
import cat.mvmike.minimalcalendarwidget.domain.header.MonthYearHeaderService
import cat.mvmike.minimalcalendarwidget.infrastructure.IntentService
import cat.mvmike.minimalcalendarwidget.infrastructure.ReceiverService
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
import java.util.*
import java.util.concurrent.TimeUnit

class MonthWidget : AppWidgetProvider() {
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        ReceiverService.registerReceivers(context)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val rv = RemoteViews(context.getPackageName(), ConfigurationService.getTheme(context).mainLayout)
        drawWidgets(context, appWidgetManager, appWidgetIds, rv)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        IntentService.processIntent(context, intent)
        forceRedraw(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        ConfigurationService.clearConfiguration(context)
        ReceiverService.unregisterReceivers(context)
    }

    companion object {
        fun forceRedraw(context: Context?) {
            if (!SystemResolver.Companion.get().isReadCalendarPermitted(context)) {
                return
            }
            val name = ComponentName(context, MonthWidget::class.java)
            val rv = RemoteViews(context.getPackageName(), ConfigurationService.getTheme(context).mainLayout)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            drawWidgets(context, appWidgetManager, appWidgetManager.getAppWidgetIds(name), rv)
        }

        private fun drawWidgets(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?, remoteViews: RemoteViews?) {
            for (appWidgetId in appWidgetIds) {
                drawWidget(context, appWidgetManager, appWidgetId, remoteViews)
            }
        }

        private fun drawWidget(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, widgetRemoteView: RemoteViews?) {
            widgetRemoteView.removeAllViews(R.id.calendar_widget)

            // LISTENER FOR WIDGET PRESS AND CONFIGURATION
            IntentService.addListeners(context, widgetRemoteView)

            // SET MONTH, YEAR AND DAY HEADERS
            MonthYearHeaderService.setMonthYearHeader(context, widgetRemoteView)
            DayHeaderService.setDayHeaders(context, widgetRemoteView)

            // GET CALENDAR EVENT INSTANCES AND SET DAYS
            val instanceSet = InstanceService.getInstancesWithTimeout(context, 200, TimeUnit.MILLISECONDS).orElse(HashSet())
            DayService.setDays(context, widgetRemoteView, instanceSet)

            // UPDATE WIDGET
            appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteView)
        }
    }
}