package com.example.reader.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReadiumModule {
    @Provides
    @Singleton
    fun provideHttpClient(): DefaultHttpClient = DefaultHttpClient()

    @Provides
    @Singleton
    fun provideAssetRetriever(
        @ApplicationContext context: Context,
        httpClient: DefaultHttpClient
    ): AssetRetriever = AssetRetriever(context.contentResolver, httpClient)

    @Provides
    @Singleton
    fun providePublicationOpener(
        @ApplicationContext context: Context,
        assetRetriever: AssetRetriever,
        httpClient: DefaultHttpClient
    ): PublicationOpener = PublicationOpener(
        publicationParser = DefaultPublicationParser(
            context = context,
            assetRetriever = assetRetriever,
            httpClient = httpClient,
            pdfFactory = null,
        )
    )
}