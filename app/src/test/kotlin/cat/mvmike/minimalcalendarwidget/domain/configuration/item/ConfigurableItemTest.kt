// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.domain.configuration.item

import cat.mvmike.minimalcalendarwidget.domain.configuration.ConfigurableItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class ConfigurableItemTest {
    @ParameterizedTest
    @ValueSource(strings = ["someRAndOmInputT", "ALL_UPPER_CASE", "alllowercase"])
    fun getDisplayValue_shouldReturnOnlyFirstLetterUpperCase(input: String) {
        assertFirstLetterUpperCaseOthersLowercase(ConfigurableItem.Companion.getDisplayValue(input))
    }

    @ParameterizedTest
    @EnumSource(value = ConfigurableItem::class)
    fun key_shouldReturnLowerCaseName(configurableItem: ConfigurableItem) {
        Assertions.assertEquals(configurableItem.name.toLowerCase(Locale.ENGLISH), configurableItem.key())
    }

        private fun assertFirstLetterUpperCaseOthersLowercase(input: String) {
            Assertions.assertTrue(Character.isUpperCase(input.get(0)))
            Assertions.assertEquals(input.substring(1), input.substring(1).toLowerCase(Locale.ENGLISH))
    }
}