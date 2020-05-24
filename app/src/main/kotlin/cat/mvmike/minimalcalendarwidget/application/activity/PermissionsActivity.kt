// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import cat.mvmike.minimalcalendarwidget.BaseTest
import cat.mvmike.minimalcalendarwidget.application.MonthWidget
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

class PermissionsActivity : Activity() {
    override fun onStart() {
        super.onStart()
        setResult(RESULT_CANCELED)
        ActivityCompat.requestPermissions(this, arrayOf<String?>(Manifest.permission.READ_CALENDAR), READ_CALENDAR_PERM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == READ_CALENDAR_PERM && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setResult(RESULT_OK)
            MonthWidget.Companion.forceRedraw(this)
        }
        finish()
    }

    companion object {
        private const val READ_CALENDAR_PERM = 225
    }
}