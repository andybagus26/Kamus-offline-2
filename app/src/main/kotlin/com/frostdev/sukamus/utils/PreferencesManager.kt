package com.frostdev.sukamus.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences("KamusPref", Context.MODE_PRIVATE)

    companion object {
        private const val FIRST_TIME_LOAD = "first_time_load"
    }

    fun getFirstTimeLoad(): Boolean {
        return pref.getBoolean(FIRST_TIME_LOAD, true)
    }

    fun setFirstTimeLoad(value: Boolean) {
        pref.edit().putBoolean(FIRST_TIME_LOAD, value).apply()
    }
}