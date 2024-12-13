package com.example.worthitometer.viewmodel

import ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worthItOMeter.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {

    val items: StateFlow<List<Item>> = repository.itemsFlow
        .map{ it.itemsList}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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