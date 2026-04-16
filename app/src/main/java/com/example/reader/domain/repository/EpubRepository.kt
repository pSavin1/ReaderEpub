package com.example.reader.domain.repository

import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Publication
import java.io.File

interface EpubRepository {
    fun loadEpubPublication(epubFile: File?): Flow<Publication?>
    fun closePublication()
}