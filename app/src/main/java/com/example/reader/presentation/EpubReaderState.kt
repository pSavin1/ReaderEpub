package com.example.reader.presentation

import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

data class EpubReaderState(
    val isLoading: Boolean = false,
    val initialLocator: Locator? = null,
    val progress: Double = 0.0,
    val publication: Publication? = null,
    val isError: Boolean = false,
)
