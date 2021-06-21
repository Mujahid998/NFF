package utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HelperMethods {

    companion object{

        fun Context.toast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        fun <T> Fragment.getNavigationResult(key: String = "result") =
                findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)

        fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
                findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

        fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
        }



    }

}