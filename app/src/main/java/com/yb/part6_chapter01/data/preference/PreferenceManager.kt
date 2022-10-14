package com.yb.part6_chapter01.data.preference

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(
    context: Context,
) {

    companion object {
        const val PREFERENCE_NAME = "part6_chapter01_pref"
        private const val DEFAULT_VALUE_STRING = ""
        const val KEY_ID_TOKEN = "ID_TOKEN"
    }

    private fun getPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private val prefs by lazy { getPreference(context) }
    private val editor by lazy { prefs.edit() }

    fun putIdToken(idToken: String) {
        editor.putString(KEY_ID_TOKEN, idToken)
        editor.apply()
    }

    fun getIdToken(): String? =
        prefs.getString(KEY_ID_TOKEN, null)

    fun removeIdToken() {
        editor.putString(KEY_ID_TOKEN, null)
        editor.apply()
    }
}