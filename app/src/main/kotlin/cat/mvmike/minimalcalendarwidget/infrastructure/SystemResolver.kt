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
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.entry.Instance
import java.time.*
import java.util.*
import kotlin.random.Random.Default.Companion

class SystemResolver private constructor() {

    // CLOCK
    fun getInstant(): Instant {
        return CLOCK_UTC_TZ.instant()
    }

    fun getSystemLocalDate(): LocalDate {
        return LocalDate.now(CLOCK_SYS_TZ)
    }

    fun getSystemLocalDateTime(): LocalDateTime {
        return LocalDateTime.now(CLOCK_SYS_TZ)
    }

    // LOCALE
    fun getLocale(context: Context): Locale {
        val locales = context.getResources().configuration.locales
        return if (!locales.isEmpty
                && SUPPORTED_LOCALES.stream().anyMatch { sl: Locale -> sl.getLanguage() == locales[0].language }) {
            locales[0]
        } else Locale.ENGLISH
    }

    // CALENDAR CONTRACT
    fun getInstances(context: Context, begin: Long, end: Long): MutableSet<Instance> {
        val instanceCursor = CalendarContract.Instances.query(context.getContentResolver(), Instance.FIELDS, begin, end)
        if (instanceCursor == null || instanceCursor.count == 0) {
            return HashSet()
        }
        val instances: MutableSet<Instance> = HashSet()
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
    fun isReadCalendarPermitted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    // MONTH YEAR HEADER
    fun createMonthYearHeader(widgetRemoteView: RemoteViews, monthAndYear: String, headerRelativeYearSize: Float) {
        val ss = SpannableString(monthAndYear)
        ss.setSpan(RelativeSizeSpan(headerRelativeYearSize), monthAndYear.length - 4, monthAndYear.length, 0)
        widgetRemoteView.setTextViewText(R.id.month_year_label, ss)
    }

    // DAY HEADER
    fun createHeaderRow(context: Context): RemoteViews {
        return getById(context, R.layout.row_header)
    }

    fun addHeaderDayToHeader(context: Context, headerRowRv: RemoteViews, text: String, layoutId: Int) {
        val dayRv = getById(context, layoutId)
        dayRv.setTextViewText(android.R.id.text1, text)
        headerRowRv.addView(R.id.row_container, dayRv)
    }

    fun addHeaderRowToWidget(widgetRv: RemoteViews, headerRowRv: RemoteViews) {
        widgetRv.addView(R.id.calendar_widget, headerRowRv)
    }

    // DAY
    fun createRow(context: Context): RemoteViews {
        return getById(context, R.layout.row_week)
    }

    fun createDay(context: Context, specificDayLayout: Int): RemoteViews {
        return getById(context, specificDayLayout)
    }

    fun addRowToWidget(widgetRv: RemoteViews, rowRv: RemoteViews) {
        widgetRv.addView(R.id.calendar_widget, rowRv)
    }

    fun getColorInstancesTodayId(context: Context): Int {
        return ContextCompat.getColor(context, R.color.instances_today)
    }

    fun getColorInstancesId(context: Context, colour: Colour): Int {
        return ContextCompat.getColor(context, colour.hexValue)
    }

    fun addDayCellRemoteView(context: Context, rowRv: RemoteViews, cellRv: RemoteViews, spanText: String, isToday: Boolean,
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
    fun getDayOfWeekTranslatedValues(context: Context): Array<String> {
        return arrayOf(
                context.getString(R.string.monday).getDisplayValue(),
                context.getString(R.string.tuesday).getDisplayValue(),
                context.getString(R.string.wednesday).getDisplayValue(),
                context.getString(R.string.thursday).getDisplayValue(),
                context.getString(R.string.friday).getDisplayValue(),
                context.getString(R.string.saturday).getDisplayValue(),
                context.getString(R.string.sunday).getDisplayValue()
        )
    }

    fun getAbbreviatedDayOfWeekTranslated(context: Context, dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> context.getString(R.string.monday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.TUESDAY -> context.getString(R.string.tuesday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.WEDNESDAY -> context.getString(R.string.wednesday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.THURSDAY -> context.getString(R.string.thursday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.FRIDAY -> context.getString(R.string.friday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.SATURDAY -> context.getString(R.string.saturday_abb).toUpperCase(Locale.ENGLISH)
            DayOfWeek.SUNDAY -> context.getString(R.string.sunday_abb).toUpperCase(Locale.ENGLISH)
        }
    }

    fun getThemeTranslatedValues(context: Context): Array<String> {
        return arrayOf(
                context.getString(R.string.black).getDisplayValue(),
                context.getString(R.string.grey).getDisplayValue(),
                context.getString(R.string.white).getDisplayValue()
        )
    }

    fun getInstancesSymbolsTranslatedValues(context: Context): Array<String> {
        return arrayOf(
                context.getString(R.string.minimal).getDisplayValue(),
                context.getString(R.string.vertical).getDisplayValue(),
                context.getString(R.string.circles).getDisplayValue(),
                context.getString(R.string.numbers).getDisplayValue(),
                context.getString(R.string.roman).getDisplayValue(),
                context.getString(R.string.binary).getDisplayValue(),
                context.getString(R.string.none).getDisplayValue()
        )
    }

    fun getInstancesSymbolsColourTranslatedValues(context: Context): Array<String> {
        return arrayOf(
                context.getString(R.string.cyan).getDisplayValue(),
                context.getString(R.string.mint).getDisplayValue(),
                context.getString(R.string.blue).getDisplayValue(),
                context.getString(R.string.green).getDisplayValue(),
                context.getString(R.string.yellow).getDisplayValue(),
                context.getString(R.string.white).getDisplayValue()
        )
    }

    private val CLOCK_UTC_TZ = Clock.systemUTC()
    private val CLOCK_SYS_TZ = Clock.systemDefaultZone()
    private val SUPPORTED_LOCALES: MutableSet<Locale> = HashSet(Arrays.asList(
            Locale.ENGLISH,
            Locale("ca"),  // catalan
            Locale("de"), // german
            Locale("es"),  // spanish
            Locale("fr"),  // french
            Locale("hr"), // croatian
            Locale("nb"),  // norwegian
            Locale("nl"),  // dutch
            Locale("ru") // russian
    ))

    @Volatile
    private var instance: SystemResolver? = null

    @Synchronized
    fun get(): SystemResolver {
        if (instance == null) {
            instance = SystemResolver()
        }
        return instance!!
    }

    // INTERNAL UTILS

    private fun getById(context: Context, layoutId: Int): RemoteViews {
        return RemoteViews(context.getPackageName(), layoutId)
    }

    private fun String.getDisplayValue(): String {
        return (this.substring(0, 1).toUpperCase(Locale.ENGLISH)
                + this.substring(1).toLowerCase(Locale.ENGLISH))
    }
}