package com.example.reader.domain.repository

import org.readium.r2.shared.publication.Publication
import java.io.File

interface EpubRepository {
    suspend fun loadEpubPublication(epubFile: File?): Publication?
    fun closePublication()
}