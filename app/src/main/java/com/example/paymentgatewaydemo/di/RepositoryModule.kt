package com.example.paymentgatewaydemo.di

import com.example.paymentgatewaydemo.network.ApiClient
import com.example.paymentgatewaydemo.repo.MyRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun provideMyRepository(apiClient: ApiClient): MyRepo {
        return MyRepo(apiClient)
    }

}