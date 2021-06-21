package utils

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import modelClasses.Categories
import modelClasses.FoodItems
import modelClasses.FoodItemsSizes
import org.json.JSONArray
import java.io.InputStream

class DataHelper {


    companion object{

        val catList= listOf("Beef", "Poultry", "Pork","Seafood",
            "Lamb & Veal", "Convenient Items","Pizzas", "Ditoni's Pasta",
            "The Organic Garden","The Garden (Veggies)","Dairy Farm",
            "Seasonings and Sauces", "Desserts","Pantry")


         fun readJsonFile(mContext: Context){

             val db = Firebase.firestore

            val arr = JSONArray(readJSONFromAsset(mContext))

            for(position in 0 until arr.length()){


                var foodSizesList:MutableList<FoodItemsSizes> = ArrayList()

                val category=arr.getJSONObject(position).getString("category")
                val name=arr.getJSONObject(position).getString("name")
                val sizePosition=arr.getJSONObject(position).getString("selectedSizePosition")
                val sizesArray=arr.getJSONObject(position).getJSONArray("sizes")

                for(sizeItem in 0 until sizesArray.length()){

                    val size=sizesArray.getJSONObject(sizeItem).getString("size")
                    val points=sizesArray.getJSONObject(sizeItem).getString("points")

                    val size1= FoodItemsSizes()
                    size1.size=size
                    size1.points=points.toFloat()

                    foodSizesList.add(size1)
                }


                val documentId=System.currentTimeMillis().toString()
                val ref=db.collection(Constants.ITEMS).document(documentId)

                val foodItem = FoodItems()
                foodItem.id=documentId
                foodItem.name=name
                foodItem.selectedSizePosition=sizePosition.toInt()
                foodItem.category=category
                foodItem.sizesList=foodSizesList


                //Log.v("FoodName",foodItem.name.toString())
                ref.set(foodItem)

            }

             val cats= Categories()
             cats.categoriesList= catList
             db.collection(Constants.CATEGORIES).document(Constants.CATEGORIES_LIST).set(cats)

        }

        private fun readJSONFromAsset(mContext: Context): String? {

            val FILE_NAME="nff.json"

            var json: String? = null
            try {
                val  inputStream: InputStream = mContext.assets.open(FILE_NAME)
                json = inputStream.bufferedReader().use{it.readText()}
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
            return json
        }
    }
}