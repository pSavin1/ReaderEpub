package com.example.reader.data

import android.content.Context
import com.example.reader.domain.repository.EpubRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File
import javax.inject.Inject

class EpubRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
): EpubRepository {

    private var publication: Publication? = null

    override fun loadEpubPublication(epubFile: File?): Flow<Publication?> = flow {
        if (publication != null) {
            emit(publication)
            return@flow
        }
        if (epubFile == null) {
            emit(null)
            return@flow
        }

        val httpClient = DefaultHttpClient()
        val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
        val asset = assetRetriever.retrieve(epubFile).getOrNull() ?: run {
            emit(null)
            return@flow
        }

        val publicationOpener = PublicationOpener(
            publicationParser = DefaultPublicationParser(
                context = context,
                assetRetriever = assetRetriever,
                httpClient = httpClient,
                pdfFactory = null,
            )
        )

        publication = publicationOpener.open(
            asset,
            allowUserInteraction = true
        ).getOrNull()

        emit(publication)
    }

    override fun closePublication() {
        publication?.close()
    }
}