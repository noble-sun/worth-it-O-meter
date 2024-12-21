import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.worthItOMeter.Item
import com.worthItOMeter.ItemList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.itemListDataStore: DataStore<ItemList> by dataStore(
    fileName = "item_list.pb",
    serializer = ItemListSerializer
)

class ItemRepository(private val dataStore: DataStore<ItemList>) {

    val itemsFlow = dataStore.data

    suspend fun updateAllItems(transform: (Item, Int) -> Item) {
        dataStore.updateData { currentList ->
            currentList.toBuilder()
                .clearItems()
                .addAllItems(
                    currentList.itemsList.mapIndexed { index, item ->
                        transform(item, index)
                    }
                ).build()
        }
    }

    fun getItem(index: Int): Flow<Item?> {
        return dataStore.data.map { itemList ->
            val items = itemList.itemsList
            if (index in items.indices) {
                items[index]
            } else {
                null
            }
        }
    }
    suspend fun addItem(newItem: Item) {
        dataStore.updateData { currentList ->
            currentList.toBuilder()
                .addItems(newItem)
                .build()
        }
    }

    suspend fun editItem(index: Int, updatedItem: Item) {
        dataStore.updateData { currentList ->
            val mutableList = currentList.itemsList.toMutableList()
            if (index in mutableList.indices) {
                mutableList[index] = updatedItem
            }
            currentList.toBuilder()
                .clearItems()
                .addAllItems(mutableList)
                .build()
        }
    }

    suspend fun deleteItem(index: Int) {
        dataStore.updateData { currentList ->
            val mutableList = currentList.itemsList.toMutableList()
            if (index in mutableList.indices) {
                mutableList.removeAt(index)
            }
            currentList.toBuilder()
                .clearItems()
                .addAllItems(mutableList)
                .build()
        }
    }
}