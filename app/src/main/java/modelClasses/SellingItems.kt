package modelClasses

import androidx.room.*
import localDataBase.ItemSizeTypeConverter



class SellingItems() {


    var id : String? = null
    var name: String? = null
    var category: String? = null
    var selectedSizePosition: Int = 0
    var orderPoints: Float = 0F
    var orderCounts: Int = 0
    var note:String? = null
    var isFree:Boolean=false

    lateinit var sellingItemsSizesList: MutableList<SellingItemsSizes>



    override fun equals(other: Any?): Boolean {
        when (other) {
            is SellingItems -> {
                return this.name == other.name
            }
            else -> return false
        }
    }


}
