import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.worthItOMeter.Item
import com.worthItOMeter.ItemList

val Context.itemListDataStore: DataStore<ItemList> by dataStore(
    fileName = "item_list.pb",
    serializer = ItemListSerializer
)

class ItemRepository(private val dataStore: DataStore<ItemList>) {

    val itemsFlow = dataStore.data

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