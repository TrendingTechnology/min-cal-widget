// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import cat.mvmike.minimalcalendarwidget.BaseTest
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
import java.util.*

enum class ConfigurableItem {
    FIRST_DAY_OF_WEEK, THEME, INSTANCES_SYMBOLS, INSTANCES_SYMBOLS_COLOUR;

    fun key(): String? {
        return name.toLowerCase(Locale.ENGLISH)
    }

    companion object {
        fun getDisplayValue(name: String?): String? {
            return (name.substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + name.substring(1).toLowerCase(Locale.ENGLISH))
        }
    }
}