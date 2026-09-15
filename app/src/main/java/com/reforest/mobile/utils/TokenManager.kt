package com.reforest.mobile.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(Constants.KEY_TOKEN, null)
    }

    fun saveUser(user: String) {
        prefs.edit().putString(Constants.KEY_USER, user).apply()
    }

    fun getUser(): String? {
        return prefs.getString(Constants.KEY_USER, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return !token.isNullOrEmpty()
    }
}