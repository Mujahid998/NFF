package modelClasses

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import localDataBase.ItemSizeTypeConverter



@Parcelize
class ReceiptModel(): Parcelable{


    var name: String? = null
    var quantity: String? = null
    var size: String? = null
    var note: String? = null


}
