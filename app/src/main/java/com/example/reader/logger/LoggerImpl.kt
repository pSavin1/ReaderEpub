package com.example.reader.logger

import android.util.Log
import com.example.reader.BuildConfig
import javax.inject.Inject

class LoggerImpl @Inject constructor(
    private val tag: String = "ReaderApp"
) : Logger {

    override fun d(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    override fun i(message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    override fun e(message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }
    }
}