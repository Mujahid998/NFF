package viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import modelClasses.FoodItems
import modelClasses.SellingItems
import repositories.RepositorySellingItems

class ItemsViewModel(context: Application, category: String) : ViewModel() {


    private var items= MutableLiveData<MutableList<FoodItems>>()


    init {

        items=RepositorySellingItems.getInstance().getItems(category)
    }


    fun getItems(): MutableLiveData<MutableList<FoodItems>> {
        return items
    }

}