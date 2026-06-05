package com.example.reader.data

import com.example.reader.domain.repository.EpubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.streamer.PublicationOpener
import javax.inject.Inject

class EpubRepositoryImpl @Inject constructor(
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    private val assetLoaderHelper: AssetLoaderHelper,
): EpubRepository {

    private var publication: Publication? = null
    private var fileName: String? = null

    override suspend fun loadEpubPublication(fileName: String): Publication? =
        withContext(Dispatchers.IO) {
            if (fileName == this@EpubRepositoryImpl.fileName && publication != null) {
                return@withContext publication
            }
            this@EpubRepositoryImpl.fileName = fileName
            val epubFile = assetLoaderHelper.loadAsset(fileName)
            val resultAsset = epubFile?.let { assetRetriever.retrieve(it) }
            resultAsset?.onSuccess {
                publication = publicationOpener.open(
                    asset = it,
                    allowUserInteraction = true
                ).getOrNull()
                return@withContext publication
            }
            null
        }

    override fun closePublication() {
        publication?.close()
        publication = null
        fileName = null
    }
}