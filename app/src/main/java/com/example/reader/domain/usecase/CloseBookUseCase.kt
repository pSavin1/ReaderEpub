package com.example.reader.domain.usecase

import com.example.reader.domain.repository.EpubRepository
import javax.inject.Inject

class CloseBookUseCase @Inject constructor(
    private val epubRepository: EpubRepository
) {
    operator fun invoke() = epubRepository.closePublication()
}