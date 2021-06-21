package viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import modelClasses.Categories
import modelClasses.OrderItem
import repositories.RepositoryCats
import repositories.RepositoryOrders

class OrdersViewModel() : ViewModel() {


    private var orders= MutableLiveData<MutableList<OrderItem>>()


    init {

        orders=RepositoryOrders.getInstance().getOrdersList()
    }


    fun getOrders(): MutableLiveData<MutableList<OrderItem>> {
        return orders
    }

}