package com.sonicwave.speakercleaner.util

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class PreferenceUtil(val context: Context) {
    companion object {
        private const val PREFERENCE_NAME = "cleaner"
        private const val TOKEN_KEY = "token"
        private const val UID_KEY = "uid"
        private const val FIRST_KEY = "first"
    }

    private val mSharedPreference =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun getToken(): String {
        return mSharedPreference.getString(TOKEN_KEY,"").orEmpty()
    }



    fun clearLogin(){
        mSharedPreference.edit {
            putString(TOKEN_KEY, "")
            putInt(UID_KEY, 0)
        }
    }

    fun setToken(token: String, token_type: String) {
        mSharedPreference.edit {
            putString(TOKEN_KEY, token_type +" "+ token)
        }
    }


    fun isFirst():Boolean{
        return mSharedPreference.getBoolean(FIRST_KEY,true)
    }

    fun setFirst(){
        mSharedPreference.edit {
            putBoolean(FIRST_KEY, false)
        }
    }

}
