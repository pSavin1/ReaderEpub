package com.example.reader.domain.usecase

import com.example.reader.domain.repository.PagePositionRepository
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

class GetPageUseCase @Inject constructor(
    private val pagePositionRepository: PagePositionRepository
) {
    operator fun invoke(): Locator? = pagePositionRepository.getPage()
}