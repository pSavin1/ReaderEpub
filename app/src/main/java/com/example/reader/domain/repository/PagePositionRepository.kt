package com.example.reader.domain.repository

import org.readium.r2.shared.publication.Locator

interface PagePositionRepository {
    fun savePage(locator: Locator)
    fun getPage(): Locator?
}