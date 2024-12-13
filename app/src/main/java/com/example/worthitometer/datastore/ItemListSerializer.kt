import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.worthItOMeter.ItemList
import java.io.InputStream
import java.io.OutputStream

object ItemListSerializer : Serializer<ItemList> {
    override val defaultValue: ItemList = ItemList.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): ItemList {
        try {
            return ItemList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ItemList, output: OutputStream) = t.writeTo(output)
}
