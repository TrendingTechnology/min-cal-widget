// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import java.util.*

enum class ConfigurableItem {
    FIRST_DAY_OF_WEEK,
    THEME,
    INSTANCES_SYMBOLS,
    INSTANCES_SYMBOLS_COLOUR;

    fun key(): String {
        return name.toLowerCase(Locale.ENGLISH)
    }
}