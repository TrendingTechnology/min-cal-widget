// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

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

// https://unicode-table.com
enum class Symbol(private val relativeSize: Float, vararg values: Char) {
    MINIMAL(1.2f, '·', '∶', '∴', '∷', '◇', '◈'), VERTICAL(1.2f, '·', '∶', '⁝', '⁞', '|'), CIRCLES(1.2f, '◔', '◑', '◕', '●', '๑'), NUMBERS(0.8f, '1', '2', '3', '4', '5', '6', '7', '8', '9', '+'), ROMAN(0.8f, 'Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ', 'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ', '∾'), BINARY(1f, '☱', '☲', '☳', '☴', '☵', '☶', '☷', '※'), NONE(1f, ' ');

    private val symbolArray: CharArray?
    fun getRelativeSize(): Float {
        return relativeSize
    }

    fun getSymbol(numOfInstances: Int): String? {
        if (numOfInstances == 0) {
            return INSTANCES_SYMBOLS_EMPTY
        }
        val max = symbolArray.size - 1
        return if (numOfInstances > max) symbolArray.get(max) else symbolArray.get(numOfInstances - 1).toString()
    }

    companion object {
        private val INSTANCES_SYMBOLS_EMPTY: String? = " "
    }

    init {
        symbolArray = Arrays.copyOf(values, values.size)
    }
}