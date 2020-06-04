// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.application.MonthWidget
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem
import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurationService
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Colour
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Symbol
import cat.mvmike.minimalcalendarwidget.domain.configuration.item.Theme
import cat.mvmike.minimalcalendarwidget.infrastructure.SystemResolver
import java.time.DayOfWeek

class ConfigurationActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.configuration)
        setHyperlinks()
        setAvailableValues()
        loadPreviousConfig()
        applyListener()
    }

    private fun setHyperlinks() {
        (findViewById<View>(R.id.source) as TextView).movementMethod = LinkMovementMethod.getInstance()
    }

    private fun applyListener() {
        val dismissButton = findViewById<Button>(R.id.applyButton)
        dismissButton.setOnClickListener {
            saveConfig()
            finish()
        }
    }

    private fun setAvailableValues() {

        // THEMES
        val adapterThemes: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                SystemResolver.get().getThemeTranslatedValues(applicationContext))
        adapterThemes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (findViewById<View>(R.id.themeSpinner) as Spinner).adapter = adapterThemes

        // WEEK DAYS
        val adapterWeekDays: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                SystemResolver.get().getDayOfWeekTranslatedValues(applicationContext))
        adapterWeekDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (findViewById<View>(R.id.startWeekDaySpinner) as Spinner).adapter = adapterWeekDays

        // SYMBOLS
        val adapterSymbols: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                SystemResolver.get().getInstancesSymbolsTranslatedValues(applicationContext))
        adapterSymbols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (findViewById<View>(R.id.symbolsSpinner) as Spinner).adapter = adapterSymbols

        // SYMBOLS COLOUR
        val adapterSymbolsColour: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                SystemResolver.get().getInstancesSymbolsColourTranslatedValues(applicationContext))
        adapterSymbolsColour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (findViewById<View>(R.id.symbolsColourSpinner) as Spinner).adapter = adapterSymbolsColour
    }

    private fun loadPreviousConfig() {

        // THEMES
        (findViewById<View>(R.id.themeSpinner) as Spinner)
                .setSelection(ConfigurationService.getTheme(applicationContext).ordinal)

        // WEEK DAYS
        (findViewById<View>(R.id.startWeekDaySpinner) as Spinner)
                .setSelection(ConfigurationService.getStartWeekDay(applicationContext).ordinal)

        // SYMBOLS
        (findViewById<View>(R.id.symbolsSpinner) as Spinner)
                .setSelection(ConfigurationService.getInstancesSymbols(applicationContext).ordinal)

        // SYMBOLS COLOUR
        (findViewById<View>(R.id.symbolsColourSpinner) as Spinner)
                .setSelection(ConfigurationService.getInstancesSymbolsColours(applicationContext).ordinal)
    }

    private fun saveConfig() {

        // THEMES
        val themesSelectedPosition = (findViewById<View>(R.id.themeSpinner) as Spinner).selectedItemPosition
        ConfigurationService[applicationContext, ConfigurableItem.THEME] = Theme.values()[themesSelectedPosition]

        // WEEK DAYS
        val weekDaySelectedPosition = (findViewById<View>(R.id.startWeekDaySpinner) as Spinner).selectedItemPosition
        ConfigurationService[applicationContext, ConfigurableItem.FIRST_DAY_OF_WEEK] = DayOfWeek.values()[weekDaySelectedPosition]

        // SYMBOLS
        val symbolsSelectedPosition = (findViewById<View>(R.id.symbolsSpinner) as Spinner).selectedItemPosition
        ConfigurationService.set(applicationContext, ConfigurableItem.INSTANCES_SYMBOLS, Symbol.values()[symbolsSelectedPosition])

        // SYMBOLS COLOUR
        val symbolsColourSelectedPosition = (findViewById<View>(R.id.symbolsColourSpinner) as Spinner).selectedItemPosition
        ConfigurationService[applicationContext, ConfigurableItem.INSTANCES_SYMBOLS_COLOUR] = Colour.values()[symbolsColourSelectedPosition]
        MonthWidget.forceRedraw(applicationContext)
    }


    fun start(context: Context) {
        val configurationIntent = Intent(context, ConfigurationActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(configurationIntent)
    }
}