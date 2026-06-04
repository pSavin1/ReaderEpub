package com.example.reader.metric

interface Metric {
    fun init()

    fun event(name: String, params: Map<String, Any>)

    fun error(name: String, params: Map<String, Any>)
}