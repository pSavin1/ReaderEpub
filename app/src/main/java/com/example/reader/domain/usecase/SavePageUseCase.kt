package com.example.reader.domain.usecase

import com.example.reader.domain.repository.PagePositionRepository
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

class SavePageUseCase @Inject constructor(
    private val pagePositionRepository: PagePositionRepository
) {
    operator fun invoke(locator: Locator) = pagePositionRepository.savePage(locator)
}