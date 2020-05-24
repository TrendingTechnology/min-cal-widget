// Copyright (c) 2019, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import android.content.Context
import android.content.SharedPreferences
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import java.time.DayOfWeek
import java.util.*

abstract class BaseTest {
    protected val context = Mockito.mock(Context::class.java)
    protected val sharedPreferences = Mockito.mock(SharedPreferences::class.java)
    protected val editor = Mockito.mock(SharedPreferences.Editor::class.java)
    protected val systemResolver = Mockito.mock(SystemResolver::class.java)

    @BeforeEach
    fun beforeEach() {
        Mockito.reset(context, sharedPreferences, editor)
        Mockito.`when`(context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
        Mockito.`when`(editor.clear()).thenReturn(editor)
        Mockito.`when`(editor.commit()).thenReturn(true)
        try {
            val instance = SystemResolver::class.java.getDeclaredField("instance")
            instance.isAccessible = true
            instance[instance] = systemResolver
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val PREFERENCES_ID: String? = "mincal_prefs"

        @BeforeAll
        fun beforeAll() {

            // force a different timezone than UTC for testing
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))
        }

        protected fun mockTheme(sharedPreferences: SharedPreferences?, theme: Theme?) {
            Mockito.`when`(sharedPreferences.getString(ConfigurableItem.THEME.key(), Theme.BLACK.name)).thenReturn(theme.name)
        }

        protected fun mockStartWeekDay(sharedPreferences: SharedPreferences?, dayOfWeek: DayOfWeek?) {
            Mockito.`when`(sharedPreferences.getString(ConfigurableItem.FIRST_DAY_OF_WEEK.key(), DayOfWeek.MONDAY.name)).thenReturn(dayOfWeek.name)
        }

        protected fun mockInstancesSymbolsColour(sharedPreferences: SharedPreferences?, colour: Colour?) {
            Mockito.`when`(sharedPreferences.getString(ConfigurableItem.INSTANCES_SYMBOLS_COLOUR.key(), Colour.CYAN.name)).thenReturn(colour.name)
        }

        protected fun mockInstancesSymbols(sharedPreferences: SharedPreferences?, symbol: Symbol?) {
            Mockito.`when`(sharedPreferences.getString(ConfigurableItem.INSTANCES_SYMBOLS.key(), Symbol.MINIMAL.name)).thenReturn(symbol.name)
        }
    }
}