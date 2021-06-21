package viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import modelClasses.Categories
import repositories.RepositoryCats

class CatsViewModel(context: Context) : ViewModel() {


    private var categories= MutableLiveData<ArrayList<String>>()


    init {

        categories=RepositoryCats.getInstance(context).getCategories()
        RepositoryCats.getInstance(context).getPantryPrice()
    }


    fun getCats(): MutableLiveData<ArrayList<String>> {
        return categories
    }

}