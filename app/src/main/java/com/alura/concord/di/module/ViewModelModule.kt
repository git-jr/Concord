package com.alura.concord.di.module

import android.content.Context
import com.alura.concord.ConcordApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext context: Context): ConcordApplication {
        return context as ConcordApplication
    }

}