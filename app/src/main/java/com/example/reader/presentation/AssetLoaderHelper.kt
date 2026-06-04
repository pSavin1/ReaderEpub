package com.example.reader.presentation

import android.content.Context
import com.example.reader.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AssetLoaderHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger,
) {
    suspend fun loadAsset(assetName: String): File? =
        withContext(Dispatchers.IO) {
            logger.d("Book loading started")
            return@withContext try {
                val file = File(context.filesDir, assetName)
                if (!file.exists()) {
                    context.assets.open(assetName).use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                logger.d("Book file loaded")
                file
            } catch (_: Exception) {
                logger.e("Book loading error")
                null
            }
        }
}