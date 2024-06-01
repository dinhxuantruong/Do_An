package com.example.room_mvvm.di

import android.content.Context
import androidx.room.Room
import com.example.datn.data.roomDatabase.DatabaseSearch
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseMainModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context,DatabaseSearch::class.java,"search_Database").build()

    @Singleton
    @Provides
    fun provideDao(database : DatabaseSearch) = database.searchDao()
}