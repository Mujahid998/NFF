package modelClasses

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import localDataBase.ItemSizeTypeConverter



@Parcelize
class FoodItems(): Parcelable{


    var id : String? = null
    var name: String? = null
    var category: String? = null
    var selectedSizePosition: Int = 0

    lateinit var sizesList: MutableList<FoodItemsSizes>


}
