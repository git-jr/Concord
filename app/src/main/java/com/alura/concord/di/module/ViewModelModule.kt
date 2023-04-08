package com.alura.concord.di.module

import androidx.lifecycle.SavedStateHandle
import com.alura.concord.database.MessageDao
import com.alura.concord.ui.chat.MessageListViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

//@Module
//@InstallIn(ViewModelComponent::class)
//class ViewModelModule {
//
//    @Module
//    @InstallIn(ViewModelComponent::class)
//    object MessageListViewModelModule {
//        @Provides
//        fun provideMessageListViewModel(
//            savedStateHandle: SavedStateHandle?, messageDao: MessageDao
//        ): MessageListViewModel {
//            return MessageListViewModel(savedStateHandle, messageDao)
//        }
//    }
//
//
//}

//@Module
//@InstallIn(SingletonComponent::class)
//class DatabaseModule {
//
//    @Singleton
//    @Provides
//    fun provideDatabase(@ApplicationContext context: Context): ConcordDatabase {
//        return Room.databaseBuilder(
//            context,
//            ConcordDatabase::class.java,
//            DATABASE_NAME
//        ).createFromAsset("database/concord.db")
//            .build()
//    }
//
//    @Provides
//    fun provideChatDao(db: ConcordDatabase): ChatDao {
//        return db.chatDao()
//    }
//
//    @Provides
//    fun provideMessageDao(db: ConcordDatabase): MessageDao {
//        return db.messageDao()
//    }
//}
