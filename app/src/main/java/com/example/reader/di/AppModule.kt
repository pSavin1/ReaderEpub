package com.example.reader.di

import com.example.reader.data.EpubRepositoryImpl
import com.example.reader.data.PagePositionRepositoryImpl
import com.example.reader.domain.repository.EpubRepository
import com.example.reader.domain.repository.PagePositionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindEpubRepository(epubRepositoryImpl: EpubRepositoryImpl): EpubRepository

    @Binds
    fun bindPagePositionRepository(pagePositionRepositoryImpl: PagePositionRepositoryImpl): PagePositionRepository
}