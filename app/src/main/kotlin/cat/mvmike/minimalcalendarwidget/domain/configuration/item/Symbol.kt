// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import java.util.*

// https://unicode-table.com
enum class Symbol(private val relativeSize: Float, vararg values: Char) {
    MINIMAL(1.2f, '·', '∶', '∴', '∷', '◇', '◈'),
    VERTICAL(1.2f, '·', '∶', '⁝', '⁞', '|'),
    CIRCLES(1.2f, '◔', '◑', '◕', '●', '๑'),
    NUMBERS(0.8f, '1', '2', '3', '4', '5', '6', '7', '8', '9', '+'),
    ROMAN(0.8f, 'Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ', 'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ', '∾'),
    BINARY(1f, '☱', '☲', '☳', '☴', '☵', '☶', '☷', '※'),
    NONE(1f, ' ');

    private val symbolArray: CharArray

    fun getRelativeSize(): Float {
        return relativeSize
    }

    fun getSymbol(numOfInstances: Int): String {
        if (numOfInstances == 0) {
            return INSTANCES_SYMBOLS_EMPTY
        }
        val max = symbolArray.size - 1
        return if (numOfInstances > max)
            symbolArray[max].toString()
        else
            symbolArray[numOfInstances - 1].toString()
    }

    private val INSTANCES_SYMBOLS_EMPTY: String = " "


    init {
        symbolArray = Arrays.copyOf(values, values.size)
    }
}