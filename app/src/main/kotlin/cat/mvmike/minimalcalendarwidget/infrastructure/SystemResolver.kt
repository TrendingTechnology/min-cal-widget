// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.provider.CalendarContract
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.ConfigurableItemTest
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
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
import java.time.*
import java.util.*

class SystemResolver private constructor() {
    // CLOCK
    fun getInstant(): Instant? {
        return CLOCK_UTC_TZ.instant()
    }

    fun getSystemLocalDate(): LocalDate? {
        return LocalDate.now(CLOCK_SYS_TZ)
    }

    fun getSystemLocalDateTime(): LocalDateTime? {
        return LocalDateTime.now(CLOCK_SYS_TZ)
    }

    // LOCALE
    fun getLocale(context: Context?): Locale? {
        val locales = context.getResources().configuration.locales
        return if (!locales.isEmpty
                && SUPPORTED_LOCALES.stream().anyMatch { sl: Locale? -> sl.getLanguage() == locales[0].language }) {
            locales[0]
        } else Locale.ENGLISH
    }

    // CALENDAR CONTRACT
    fun getInstances(context: Context?, begin: Long, end: Long): MutableSet<Instance?>? {
        val instanceCursor = CalendarContract.Instances.query(context.getContentResolver(), Instance.Companion.FIELDS, begin, end)
        if (instanceCursor == null || instanceCursor.count == 0) {
            return null
        }
        val instances: MutableSet<Instance?> = HashSet()
        while (instanceCursor.moveToNext()) {
            instances.add(Instance(
                    instanceCursor.getLong(0),
                    instanceCursor.getLong(1),
                    instanceCursor.getInt(2),
                    instanceCursor.getInt(3)
            ))
        }
        instanceCursor.close()
        return instances
    }

    // CONTEXT COMPAT
    fun isReadCalendarPermitted(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    // MONTH YEAR HEADER
    fun createMonthYearHeader(widgetRemoteView: RemoteViews?, monthAndYear: String?, headerRelativeYearSize: Float) {
        val ss = SpannableString(monthAndYear)
        ss.setSpan(RelativeSizeSpan(headerRelativeYearSize), monthAndYear.length - 4, monthAndYear.length, 0)
        widgetRemoteView.setTextViewText(R.id.month_year_label, ss)
    }

    // DAY HEADER
    fun createHeaderRow(context: Context?): RemoteViews? {
        return getById(context, R.layout.row_header)
    }

    fun addHeaderDayToHeader(context: Context?, headerRowRv: RemoteViews?, text: String?, layoutId: Int) {
        val dayRv = getById(context, layoutId)
        dayRv.setTextViewText(android.R.id.text1, text)
        headerRowRv.addView(R.id.row_container, dayRv)
    }

    fun addHeaderRowToWidget(widgetRv: RemoteViews?, headerRowRv: RemoteViews?) {
        widgetRv.addView(R.id.calendar_widget, headerRowRv)
    }

    // DAY
    fun createRow(context: Context?): RemoteViews? {
        return getById(context, R.layout.row_week)
    }

    fun createDay(context: Context?, specificDayLayout: Int): RemoteViews? {
        return getById(context, specificDayLayout)
    }

    fun addRowToWidget(widgetRv: RemoteViews?, rowRv: RemoteViews?) {
        widgetRv.addView(R.id.calendar_widget, rowRv)
    }

    fun getColorInstancesTodayId(context: Context?): Int {
        return ContextCompat.getColor(context, R.color.instances_today)
    }

    fun getColorInstancesId(context: Context?, colour: Colour?): Int {
        return ContextCompat.getColor(context, colour.getHexValue())
    }

    fun addDayCellRemoteView(context: Context?, rowRv: RemoteViews?, cellRv: RemoteViews?, spanText: String?, isToday: Boolean,
                             isSingleDigitDay: Boolean, symbolRelativeSize: Float, instancesColor: Int) {
        val daySpSt = SpannableString(spanText)
        daySpSt.setSpan(StyleSpan(Typeface.BOLD), spanText.length - 1, spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (isSingleDigitDay) {
            daySpSt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.alpha)), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (isToday) {
            daySpSt.setSpan(StyleSpan(Typeface.BOLD), 0, spanText.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        daySpSt.setSpan(ForegroundColorSpan(instancesColor), spanText.length - 1, spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        daySpSt.setSpan(RelativeSizeSpan(symbolRelativeSize), spanText.length - 1, spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        cellRv.setTextViewText(android.R.id.text1, daySpSt)
        rowRv.addView(R.id.row_container, cellRv)
    }

    // TRANSLATIONS
    fun getDayOfWeekTranslatedValues(context: Context?): Array<String?>? {
        return arrayOf(
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.monday)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.tuesday)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.wednesday)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.thursday)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.friday)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.saturday)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.sunday))
        )
    }

    fun getAbbreviatedDayOfWeekTranslated(context: Context?, dayOfWeek: DayOfWeek?): String? {
        when (dayOfWeek) {
            DayOfWeek.MONDAY -> return context.getString(R.string.monday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.TUESDAY -> return context.getString(R.string.tuesday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.WEDNESDAY -> return context.getString(R.string.wednesday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.THURSDAY -> return context.getString(R.string.thursday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.FRIDAY -> return context.getString(R.string.friday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.SATURDAY -> return context.getString(R.string.saturday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.SUNDAY -> return context.getString(R.string.sunday_abb).toUpperCase(Locale.ENGLISH)
        }
        return null
    }

    fun getThemeTranslatedValues(context: Context?): Array<String?>? {
        return arrayOf(
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.black)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.grey)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.white))
        )
    }

    fun getInstancesSymbolsTranslatedValues(context: Context?): Array<String?>? {
        return arrayOf(
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.minimal)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.vertical)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.circles)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.numbers)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.roman)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.binary)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.none))
        )
    }

    fun getInstancesSymbolsColourTranslatedValues(context: Context?): Array<String?>? {
        return arrayOf(
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.cyan)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.mint)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.blue)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.green)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.yellow)),
                ConfigurableItem.Companion.getDisplayValue(context.getString(R.string.white))
        )
    }

    companion object {
        private val CLOCK_UTC_TZ = Clock.systemUTC()
        private val CLOCK_SYS_TZ = Clock.systemDefaultZone()
        private val SUPPORTED_LOCALES: MutableSet<Locale?>? = HashSet(Arrays.asList(
                Locale.ENGLISH,
                Locale("ca"),  // catalan
                Locale("es"),  // spanish
                Locale("fr"),  // french
                Locale("nb"),  // norwegian
                Locale("nl"),  // dutch
                Locale("ru") // russian
        ))

        @Volatile
        private var instance: SystemResolver? = null

        @Synchronized
        fun get(): SystemResolver? {
            if (instance == null) {
                instance = SystemResolver()
            }
            return instance
        }

        // INTERNAL UTILS
        private fun getById(context: Context?, layoutId: Int): RemoteViews? {
            return RemoteViews(context.getPackageName(), layoutId)
        }
    }
}