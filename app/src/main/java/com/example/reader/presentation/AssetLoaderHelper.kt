package com.example.reader.presentation

import android.content.Context
import android.util.Log
import com.example.reader.Const.LOG_TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AssetLoaderHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun loadAsset(assetName: String): File? =
        withContext(Dispatchers.IO) {
            Log.d(LOG_TAG, "Book loading started")
            return@withContext try {
                val file = File(context.filesDir, assetName)
                if (!file.exists()) {
                    context.assets.open(assetName).use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                Log.d(LOG_TAG, "Book file loaded")
                file
            } catch (_: Exception) {
                Log.e(LOG_TAG, "Book loading error")
                null
            }
        }
    }