package com.test.data.di

import com.test.data.repositories.VoiceRepositoryImpl
import com.test.domain.repositories.VoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun provideAccountRepository(): VoiceRepository = VoiceRepositoryImpl()
}