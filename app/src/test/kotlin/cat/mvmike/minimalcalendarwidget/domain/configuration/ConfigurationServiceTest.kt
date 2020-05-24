// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration

import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mockito
import java.time.DayOfWeek

internal class ConfigurationServiceTest : BaseTest() {
    @Test
    fun clearConfiguration_shouldRemoveAllApplicationPreferences() {
        ConfigurationService.clearConfiguration(context)
        Mockito.verify(editor, Mockito.times(1)).clear()
        Mockito.verify(editor, Mockito.times(1)).apply()
        Mockito.verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = Theme::class)
    fun getTheme_shouldReturnSharedPreferencesValue(theme: Theme?) {
        BaseTest.Companion.mockTheme(sharedPreferences, theme)
        Assertions.assertEquals(theme, ConfigurationService.getTheme(context))
        Mockito.verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun getStartWeekDay_shouldReturnSharedPreferencesValue(dayOfWeek: DayOfWeek?) {
        BaseTest.Companion.mockStartWeekDay(sharedPreferences, dayOfWeek)
        Assertions.assertEquals(dayOfWeek, ConfigurationService.getStartWeekDay(context))
        Mockito.verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = Symbol::class)
    fun getInstancesSymbols_shouldReturnSharedPreferencesValue(symbol: Symbol?) {
        BaseTest.Companion.mockInstancesSymbols(sharedPreferences, symbol)
        Assertions.assertEquals(symbol, ConfigurationService.getInstancesSymbols(context))
        Mockito.verifyNoMoreInteractions(editor)
    }

    @ParameterizedTest
    @EnumSource(value = Colour::class)
    fun getInstancesSymbolsColours_shouldReturnSharedPreferencesValue(colour: Colour?) {
        BaseTest.Companion.mockInstancesSymbolsColour(sharedPreferences, colour)
        Assertions.assertEquals(colour, ConfigurationService.getInstancesSymbolsColours(context))
        Mockito.verifyNoMoreInteractions(editor)
    }
}