// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.R
import java.time.DayOfWeek

enum class Theme(private val mainLayout: Int, private val cellHeader: Int, private val cellHeaderSaturday: Int, private val cellHeaderSunday: Int, private val cellDay: Int,
                 private val cellDayThisMonth: Int, private val cellDaySaturday: Int, private val cellDaySunday: Int, private val cellDayToday: Int,
                 private val cellDaySaturdayToday: Int, private val cellDaySundayToday: Int) {
    BLACK(
            R.layout.widget_black,
            R.layout.black_cell_header,
            R.layout.black_cell_header_saturday,
            R.layout.black_cell_header_sunday,
            R.layout.black_cell_day,
            R.layout.black_cell_day_this_month,
            R.layout.black_cell_day_saturday,
            R.layout.black_cell_day_sunday,
            R.layout.black_cell_day_today,
            R.layout.black_cell_day_saturday_today,
            R.layout.black_cell_day_sunday_today
    ),
    GREY(
            R.layout.widget_grey,
            R.layout.grey_cell_header,
            R.layout.grey_cell_header_saturday,
            R.layout.grey_cell_header_sunday,
            R.layout.grey_cell_day,
            R.layout.grey_cell_day_this_month,
            R.layout.grey_cell_day_saturday,
            R.layout.grey_cell_day_sunday,
            R.layout.grey_cell_day_today,
            R.layout.grey_cell_day_saturday_today,
            R.layout.grey_cell_day_sunday_today
    ),
    WHITE(
            R.layout.widget_white,
            R.layout.white_cell_header,
            R.layout.white_cell_header_saturday,
            R.layout.white_cell_header_sunday,
            R.layout.white_cell_day,
            R.layout.white_cell_day_this_month,
            R.layout.white_cell_day_saturday,
            R.layout.white_cell_day_sunday,
            R.layout.white_cell_day_today,
            R.layout.white_cell_day_saturday_today,
            R.layout.white_cell_day_sunday_today
    );

    fun getMainLayout(): Int {
        return mainLayout
    }

    fun getCellHeader(): Int {
        return cellHeader
    }

    fun getCellHeaderSaturday(): Int {
        return cellHeaderSaturday
    }

    fun getCellHeaderSunday(): Int {
        return cellHeaderSunday
    }

    fun getCellToday(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.SATURDAY -> cellDaySaturdayToday
            DayOfWeek.SUNDAY -> cellDaySundayToday
            else -> cellDayToday
        }
    }

    fun getCellThisMonth(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.SATURDAY -> cellDaySaturday
            DayOfWeek.SUNDAY -> cellDaySunday
            else -> cellDayThisMonth
        }
    }

    fun getCellNotThisMonth(): Int {
        return cellDay
    }

}