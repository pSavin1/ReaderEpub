package com.example.reader.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.usecase.CloseBookUseCase
import com.example.reader.domain.usecase.GetPageUseCase
import com.example.reader.domain.usecase.LoadBookUseCase
import com.example.reader.domain.usecase.SavePageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class EpubReaderViewModel @Inject constructor(
    private val getPageUseCase: GetPageUseCase,
    private val savePageUseCase: SavePageUseCase,
    private val loadBookUseCase: LoadBookUseCase,
    private val closeBookUseCase: CloseBookUseCase,
    @ApplicationContext
    private val context: Context,
): ViewModel() {

    private val _state = MutableStateFlow(EpubReaderState())
    val state = _state.asStateFlow()

    fun onPageChanged(locator: Locator) {
        _state.update {
            it.copy(
                progress = locator.locations.totalProgression ?: 0.0,
            )
        }
        savePageUseCase(locator)
    }

    fun onLoadBook(fileName: String) {
        _state.update {
            it.copy(isLoading = true)
        }
        val bookFile = try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                context.assets.open(fileName).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            file
        } catch (_: Exception) {
            _state.update {
                it.copy(isError = true)
            }
            null
        }

        viewModelScope.launch {
            loadBookUseCase(bookFile).collect { publication ->
                _state.update {
                    it.copy(
                        publication = publication,
                    )
                }
            }
            try {
                val locator = getPageUseCase()
                _state.update {
                    it.copy(
                        initialLocator = locator,
                        progress = locator?.locations?.totalProgression ?: 0.0,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    override fun onCleared() {
        closeBookUseCase()
    }
}