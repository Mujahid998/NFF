package repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import modelClasses.FoodItems
import modelClasses.OrderItem
import modelClasses.SellingItems
import modelClasses.SellingItemsSizes
import utils.Constants


class RepositoryOrders {

    companion object{

        var repositoryOrders: RepositoryOrders? =null

        fun getInstance():RepositoryOrders{

            if(repositoryOrders==null){
                repositoryOrders= RepositoryOrders()
            }

            return repositoryOrders as RepositoryOrders
        }
    }

    private val ordersList: MutableList<OrderItem> = ArrayList()
    private val db = Firebase.firestore





    fun getOrdersList(): MutableLiveData<MutableList<OrderItem>> {

        ordersList.clear()

        val data=MutableLiveData<MutableList<OrderItem>>()

        db.collection(Constants.ORDERS).
        get().addOnSuccessListener { documents ->

            for(document in documents){

                val order = document.toObject(OrderItem::class.java)
                ordersList.add(order)


            }

            data.value=ordersList
        }



        return data
    }








}