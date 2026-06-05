package com.example.reader.domain.usecase

import com.example.reader.domain.repository.EpubRepository
import java.io.File
import javax.inject.Inject

class LoadBookUseCase @Inject constructor(
    private val epubRepository: EpubRepository
) {
    suspend operator fun invoke(fileName: String) = epubRepository.loadEpubPublication(fileName)
}