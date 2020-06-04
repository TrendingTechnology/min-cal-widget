// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.application.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import cat.mvmike.minimalcalendarwidget.application.MonthWidget

class PermissionsActivity: Activity() {

    private val READ_CALENDAR_PERM = 225

    override fun onStart() {
        super.onStart()
        setResult(RESULT_CANCELED)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), READ_CALENDAR_PERM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == READ_CALENDAR_PERM && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setResult(RESULT_OK)
            MonthWidget.forceRedraw(this)
        }
        finish()
    }
}