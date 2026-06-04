package com.example.reader.logger

interface Logger {
    fun d(message: String)
    fun i(message: String)
    fun e(message: String, throwable: Throwable? = null)
}