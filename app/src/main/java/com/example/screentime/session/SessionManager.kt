package com.example.screentime.session

import androidx.core.content.edit

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserId(id: Int) {
        prefs.edit { putInt("userId", id) }
    }

    fun getUserId(): Int {
        return prefs.getInt("userId", -1)
    }

    fun saveUserName(name: String?) {
        prefs.edit { putString("userName", name) }
    }

    fun getUserName(): String? {
        return prefs.getString("userName", null)
    }

    fun saveUserEmail(email: String?) {
        prefs.edit { putString("userEmail", email) }
    }

    fun getUserEmail(): String? {
        return prefs.getString("userEmail", null)
    }

    fun saveUserDesc(desc: String?) {
        prefs.edit { putString("userDesc", desc) }
    }

    fun getUserDesc(): String? {
        return prefs.getString("userDesc", null)
    }

    // EL BLOB (tipado) de la foto puede joder la eficiencia del funcionamiento del SessionManager
    fun clear() {
        prefs.edit { clear() }
    }
}
