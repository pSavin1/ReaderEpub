package com.example.reader.di

import com.example.reader.data.EpubRepositoryImpl
import com.example.reader.data.PagePositionRepositoryImpl
import com.example.reader.domain.repository.EpubRepository
import com.example.reader.domain.repository.PagePositionRepository
import com.example.reader.logger.Logger
import com.example.reader.logger.LoggerImpl
import com.example.reader.metric.Metric
import com.example.reader.metric.MetricImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppBinds {
    @Binds
    @Singleton
    fun bindEpubRepository(epubRepositoryImpl: EpubRepositoryImpl): EpubRepository

    @Binds
    fun bindPagePositionRepository(pagePositionRepositoryImpl: PagePositionRepositoryImpl): PagePositionRepository

    @Binds
    @Singleton
    fun bindMetric(metricImpl: MetricImpl): Metric
}

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideLogger(): Logger = LoggerImpl()
}