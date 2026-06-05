package com.example.reader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.AssetLoaderHelper
import com.example.reader.domain.usecase.CloseBookUseCase
import com.example.reader.domain.usecase.GetPageUseCase
import com.example.reader.domain.usecase.LoadBookUseCase
import com.example.reader.domain.usecase.SavePageUseCase
import com.example.reader.logger.Logger
import com.example.reader.metric.MetricImpl
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
    private val metric: MetricImpl,
    private val logger: Logger,
): ViewModel() {

    private val _state = MutableStateFlow(EpubReaderState())
    val state = _state.asStateFlow()

    fun onPageChanged(locator: Locator) {
        _state.update {
            it.copy(
                progress = locator.locations.totalProgression ?: 0.0,
            )
        }
        logger.d("Page changed")
        metric.event("page_changed", mapOf("page" to locator.locations.position.toString()))
        savePageUseCase(locator)
    }

    fun onLoadBook(fileName: String) {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val publication = loadBookUseCase(fileName)

            if (publication != null) {
                logger.d("Book file parsed")
                metric.event("book_opened", emptyMap())

                val locator = try {
                    val result = getPageUseCase()
                    logger.d("Page loaded")
                    result
                } catch (e: Exception) {
                    logger.d("Page loading error")
                    metric.error(
                        "page_loading_error",
                        mapOf("error" to e.localizedMessage.orEmpty())
                    )
                    e.printStackTrace()
                    null
                }
                _state.update {
                    it.copy(
                        publication = publication,
                        initialLocator = locator,
                        progress = locator?.locations?.totalProgression ?: 0.0,
                        isLoading = false,
                    )
                }
            } else {
                logger.d("Publication loading error")
                metric.error("book_loading_error", mapOf("error" to "Publication is null"))
                _state.update {
                    it.copy(
                        isError = true,
                        isLoading = false,
                        publication = null,
                        initialLocator = null,
                        progress = 0.0,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        closeBookUseCase()
        logger.d("Book closed")
        metric.event("book_closed", emptyMap())
    }
}