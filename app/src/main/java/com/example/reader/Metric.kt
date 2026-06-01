package com.example.reader

import android.util.Log
import com.example.reader.Const.LOG_TAG
import javax.inject.Inject

class Metric @Inject constructor() {
    fun init() {
        // Инициализация метрики
        Log.d(LOG_TAG, "Metric initialized")
    }

    fun event(name: String, params: Map<String, Any> = emptyMap()) {
        // Отправка события
        Log.d(LOG_TAG, "Metric event sent: $name, params: $params")
    }

    fun error(name: String, params: Map<String, Any> = emptyMap()) {
        // Отправка события ошибки
        Log.d(LOG_TAG, "Metric error sent: $name, params: $params")
    }
}