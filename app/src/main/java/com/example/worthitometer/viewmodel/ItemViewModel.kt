package com.example.worthitometer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worthitometer.datastore.ItemRepository
import com.worthItOMeter.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {

    val items: StateFlow<List<Item>> = repository.itemsFlow
        .map{ it.itemsList}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateItems(transform: (Item, Int) -> Item) {
        viewModelScope.launch {
            repository.updateAllItems(transform)
        }
    }

    fun getItem(index: Int): Flow<Item?> {
        return repository.getItem(index)
    }
    fun addItem(product: String, productPrice: Float, boughtDate: String, perDayValue: Float) {
        val newItem = Item.newBuilder()
            .setProduct(product)
            .setProductPrice(productPrice)
            .setBoughtDate(boughtDate)
            .setPerDayValue(perDayValue)
            .build()

        viewModelScope.launch {
            repository.addItem(newItem)
        }
    }

    fun editItem(index: Int, updatedItem: Item) {
        viewModelScope.launch {
            repository.editItem(index, updatedItem)
        }
    }

    fun deleteItem(index: Int) {
        viewModelScope.launch {
            repository.deleteItem(index)
        }
    }

}