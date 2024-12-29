package com.example.worthitometer.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import com.worthItOMeter.ItemList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideItemRepository(
        @ApplicationContext context: Context
    ): ItemRepository {
        return ItemRepository(context.itemListDataStore)
    }


    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<ItemList> {
        return context.itemListDataStore
    }
}