package com.slack.exercise.extensions

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.slack.exercise.R

/**
 * update the denied list with [searchTerm]. This is called when search api returns not found
 */
fun Context.addTermToDeniedList(searchTerm: String){
    val listOfCurrentTerms = retrieveDeniedListFromPref()
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val editor = sharedPreferences.edit()
    val newList = mutableListOf<String>().apply {
        addAll(listOfCurrentTerms)
        add(searchTerm)
    }

    editor.putString(getString(R.string.deny_list_pref_key), Gson().toJson(newList))
    editor.apply()
}

fun Context.retrieveDeniedListFromPref() : List<String> {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val jsonList = sharedPreferences.getString(getString(R.string.deny_list_pref_key), "")
    val stringListType = object : TypeToken<List<String>>() {}.type
    return Gson().fromJson<List<String>>(jsonList, stringListType) ?: emptyList()
}