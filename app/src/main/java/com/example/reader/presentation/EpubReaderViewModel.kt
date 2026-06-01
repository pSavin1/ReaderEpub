package com.example.reader.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.Const.LOG_TAG
import com.example.reader.Metric
import com.example.reader.domain.usecase.CloseBookUseCase
import com.example.reader.domain.usecase.GetPageUseCase
import com.example.reader.domain.usecase.LoadBookUseCase
import com.example.reader.domain.usecase.SavePageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

@HiltViewModel
class EpubReaderViewModel @Inject constructor(
    private val getPageUseCase: GetPageUseCase,
    private val savePageUseCase: SavePageUseCase,
    private val loadBookUseCase: LoadBookUseCase,
    private val closeBookUseCase: CloseBookUseCase,
    private val assetLoaderHelper: AssetLoaderHelper,
    private val metric: Metric,
): ViewModel() {

    private val _state = MutableStateFlow(EpubReaderState())
    val state = _state.asStateFlow()

    fun onPageChanged(locator: Locator) {
        _state.update {
            it.copy(
                progress = locator.locations.totalProgression ?: 0.0,
            )
        }
        Log.d(LOG_TAG, "Page changed")
        metric.event("page_changed", mapOf("page" to locator.locations.position.toString()))
        savePageUseCase(locator)
    }

    fun onLoadBook(fileName: String) {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val assetFile = assetLoaderHelper.loadAsset(fileName)
            if (assetFile == null) {
                _state.update {
                    it.copy(isError = true)
                }
            } else {
                val publication = loadBookUseCase(assetFile)
                if (publication != null) {
                    Log.d(LOG_TAG, "Book file parsed")
                    metric.event("book_opened")
                    _state.update {
                        it.copy(
                            publication = publication,
                        )
                    }
                    try {
                        val locator = getPageUseCase()
                        Log.d(LOG_TAG, "Page loaded")
                        _state.update {
                            it.copy(
                                initialLocator = locator,
                                progress = locator?.locations?.totalProgression ?: 0.0,
                            )
                        }
                    } catch (e: Exception) {
                        Log.d(LOG_TAG, "Page loading error")
                        metric.error("page_loading_error: ${e.localizedMessage}")
                        e.printStackTrace()
                    }
                    _state.update {
                        it.copy(isLoading = false)
                    }
                } else {
                    Log.d(LOG_TAG, "Publication loading error")
                    metric.error("book_loading_error")
                    _state.update {
                        it.copy(isError = true)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        closeBookUseCase()
        Log.d(LOG_TAG, "Book closed")
        metric.event("book_closed")
    }
}