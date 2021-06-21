package repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.nff.ApplicationClass
import modelClasses.Categories
import modelClasses.PantryPrice
import utils.Constants
import utils.Network
import java.util.*
import kotlin.collections.ArrayList

class RepositoryCats(context: Context) {

    val mContext=context


    companion object{

        fun getInstance(context: Context):RepositoryCats{

            var repositoryCats: RepositoryCats? =null

            if(repositoryCats==null){
                repositoryCats= RepositoryCats(context)
            }

            return repositoryCats as RepositoryCats
        }
    }




    private val catsList: ArrayList<String> = ArrayList()
    private val db = Firebase.firestore


   /* private val settings = firestoreSettings {

        isPersistenceEnabled=true

    }


    init {

        db.firestoreSettings=settings

    }*/

    fun getCategories(): MutableLiveData<ArrayList<String>> {


        val source: Source = if(Network.isNetworkAvailable(mContext)){
            Source.SERVER
        } else {
            Source.CACHE
        }

        catsList.clear()

        val data=MutableLiveData<ArrayList<String>>()

        db.collection(Constants.CATEGORIES).document(Constants.CATEGORIES_LIST).
            get().addOnSuccessListener { document ->

            val cat = document?.toObject(Categories::class.java)

            if (cat != null) {

                cat.categoriesList?.forEach {
                    catsList.add(it)
                }

                Collections.sort(catsList, String.CASE_INSENSITIVE_ORDER);

                data.value=catsList
            }
        }


        return data
    }


    fun getPantryPrice(){

        db.collection(Constants.PANTRY_PRICE).document(Constants.PRICE).
        get().addOnSuccessListener { document ->

            val obj = document?.toObject(PantryPrice::class.java)

            if (obj != null) {
                obj.mPrice?.let {
                    ApplicationClass.PANTRY_PRICE=it.toInt()
                }
            }

        }
    }



}