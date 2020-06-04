// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import java.time.DayOfWeek

object ConfigurationService {

    private const val PREFERENCES_ID: String = "mincal_prefs"

    fun clearConfiguration(context: Context) {
        getConfiguration(context).edit().clear().apply()
    }

    fun getTheme(context: Context): Theme {
        return Theme.valueOf(getEnumString(context, ConfigurableItem.THEME, Theme.BLACK))
    }

    fun getStartWeekDay(context: Context): DayOfWeek {
        return DayOfWeek.valueOf(getEnumString(context, ConfigurableItem.FIRST_DAY_OF_WEEK, DayOfWeek.MONDAY))
    }

    fun getInstancesSymbols(context: Context): Symbol {
        return Symbol.valueOf(getEnumString(context, ConfigurableItem.INSTANCES_SYMBOLS, Symbol.MINIMAL))
    }

    fun getInstancesSymbolsColours(context: Context): Colour {
        return Colour.valueOf(getEnumString(context, ConfigurableItem.INSTANCES_SYMBOLS_COLOUR, Colour.CYAN))
    }

    operator fun <T> set(context: Context, configurableItem: ConfigurableItem, value: T) {
        getConfiguration(context).edit().putString(configurableItem.key(), (value as Enum<*>).name).apply()
    }

    private fun getEnumString(context: Context, configurableItem: ConfigurableItem, defaultValue: Enum<*>): String {
        return getConfiguration(context).getString(configurableItem.key(), defaultValue.name)!!
    }

    private fun getConfiguration(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE)
    }
}