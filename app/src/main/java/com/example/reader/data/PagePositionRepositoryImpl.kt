package com.example.reader.data

import com.example.reader.data.cache.PagesSharedPreferences
import com.example.reader.domain.repository.PagePositionRepository
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

class PagePositionRepositoryImpl @Inject constructor(
    private val preferences: PagesSharedPreferences,
): PagePositionRepository {

    override fun savePage(locator: Locator) = preferences.savePage(locator)

    override fun getPage(): Locator? = preferences.getPage()
}