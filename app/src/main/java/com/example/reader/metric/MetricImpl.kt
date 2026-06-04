package com.example.reader.metric

import android.util.Log
import javax.inject.Inject

class MetricImpl @Inject constructor(): Metric {
    override fun init() {
        // Инициализация метрики
        Log.d(LOG_TAG, "Metric initialized")
    }

    override fun event(name: String, params: Map<String, Any>) {
        // Отправка события
        Log.d(LOG_TAG, "Metric event sent: $name, params: $params")
    }

    override fun error(name: String, params: Map<String, Any>) {
        // Отправка события ошибки
        Log.d(LOG_TAG, "Metric error sent: $name, params: $params")
    }

    private companion object {
        const val LOG_TAG = "READER"
    }
}