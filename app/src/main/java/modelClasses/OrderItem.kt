package modelClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class OrderItem(): Parcelable {


    var orderNumber: String? = null
    var customerName: String? = null
    var customerEmail: String? = null
    var date: String? = null
    var orderPrice: Float = 0F
    var discount: Float = 0F


    var sellingItemsList: MutableList<SellingItems>? = null


    override fun equals(other: Any?): Boolean {
        when (other) {
            is OrderItem -> {
                return this.orderNumber == other.orderNumber
            }
            else -> return false
        }
    }


}
