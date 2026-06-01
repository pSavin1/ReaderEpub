package com.example.reader.data

import com.example.reader.domain.repository.EpubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.streamer.PublicationOpener
import java.io.File
import javax.inject.Inject

class EpubRepositoryImpl @Inject constructor(
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
): EpubRepository {

    private var publication: Publication? = null

    override suspend fun loadEpubPublication(epubFile: File?): Publication? =
        withContext(Dispatchers.IO) {
            if (publication != null) {
                publication
            }
            if (epubFile == null) {
                null
            } else {
                val asset = assetRetriever.retrieve(epubFile)
                when {
                    asset.isSuccess -> {
                        publication = publicationOpener.open(
                            asset = asset.getOrNull() ?: return@withContext null,
                            allowUserInteraction = true
                        ).getOrNull()
                        publication
                    }
                    else -> null
                }
            }
        }

    override fun closePublication() {
        publication?.close()
    }
}