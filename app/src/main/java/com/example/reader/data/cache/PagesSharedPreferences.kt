package com.example.reader.data.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

class PagesSharedPreferences @Inject constructor(
    @ApplicationContext
    context: Context,
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun savePage(locator: Locator) {
        prefs.edit {
            putString(KEY_PAGE, locator.toJSON().toString())
        }
    }

    fun getPage(): Locator? {
        return Locator.fromJSON(JSONObject(prefs.getString(KEY_PAGE, "").orEmpty()))
    }

    companion object {
        private const val PREF_NAME = "book_prefs"
        private const val KEY_PAGE = "current_page"
    }
}