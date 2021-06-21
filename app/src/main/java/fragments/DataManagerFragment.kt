package fragments



import adapters.UpdateAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nff.R
import kotlinx.android.synthetic.main.dialog_delete_confirmation.*
import kotlinx.android.synthetic.main.dialog_delete_confirmation.title
import kotlinx.android.synthetic.main.dialog_update_item_name.*
import kotlinx.android.synthetic.main.fragment_data_manager.*
import kotlinx.android.synthetic.main.fragment_home.cats_recyclerView
import utils.Constants
import utils.HelperMethods.Companion.toast
import viewModels.CatsViewModel
import viewModels.CatsViewModelFactory


class DataManagerFragment : BaseFragment(), UpdateAdapter.ClickListener {



    private lateinit var catsViewModel: CatsViewModel
    private lateinit var updateAdapter: UpdateAdapter
    private var catsList: MutableList<String> = ArrayList()
    private lateinit var categoryDialog: Dialog
    private lateinit var confirmationDialog: Dialog
    private var itemPosition:Int=0
    private val UPDATE=0
    private val ADD=1
    private var actionType=UPDATE
    private val db = Firebase.firestore



    override fun onCreateView(
        inflater:
        LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        catsViewModel = ViewModelProvider(
            this,
            CatsViewModelFactory(requireContext())
        ).get(
            CatsViewModel::class.java
        )


        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_data_manager)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initViewModel()
            initViews()
            initNameUpdateDialog()
            initConfirmationDialog()

        }






    }



    private fun initViews(){

        val title="Manage categories"
        categories_name.text=title

        val navController=findNavController()

        back_btn.setOnClickListener { navController.navigateUp() }

        add_btn.setOnClickListener {
            showCatDialog(ADD)
        }


    }

    private fun initViewModel(){

        catsViewModel.getCats().observe(viewLifecycleOwner,
            Observer<MutableList<String>> { catsList ->

                this.catsList=catsList
                initAdapter(catsList)

                Log.v("BeefList", "size " + catsList.size)

            })
    }



    private fun initAdapter(catsList: List<String>){


        updateAdapter= UpdateAdapter(false,requireContext(),catsList)

        update_recyclerView.apply {
            adapter=updateAdapter
            setHasFixedSize(true)
        }

        updateAdapter.setOnItemClickListener(this)
    }


    override fun onDataItemClick(position: Int, v: View) {

        itemPosition=position

        when(v.id) {

            R.id.edit_btn -> {

                showCatDialog(UPDATE)
            }
            R.id.delete_btn -> {

                confirmationDialog.show()
            }
            else ->
            {
                navigate(position)
            }

        }



    }


    private fun navigate(position: Int){

        val navController=findNavController()

        val directions=DataManagerFragmentDirections.dataManagerToItemsManager(
            catsList.get(position)
        )

        navController.navigate(directions)
    }


    private fun initConfirmationDialog() {

        confirmationDialog = Dialog(requireContext())
        confirmationDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmationDialog.setContentView(R.layout.dialog_delete_confirmation)
        confirmationDialog.setCancelable(false)

        confirmationDialog.yesDelete.setOnClickListener(View.OnClickListener {

            deleteCat(catsList.get(itemPosition))
            confirmationDialog.dismiss()
        })

        confirmationDialog.cancelDelete.setOnClickListener(View.OnClickListener {
            confirmationDialog.dismiss()
        })


    }

    private fun initNameUpdateDialog() {

        categoryDialog = Dialog(requireContext())
        categoryDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        categoryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        categoryDialog.setContentView(R.layout.dialog_update_item_name)
        categoryDialog.setCancelable(false)

        categoryDialog.addNameBtn  .setOnClickListener(View.OnClickListener {

            val name = categoryDialog.item_name_box.text.toString()

            if (name.isEmpty()) {
                requireContext().toast("Enter name")
            } else {
                categoryDialog.dismiss()
                if(actionType==ADD){ addCat(name)}else{updateCat(name)}
            }

        })

        categoryDialog.cancelNameBtn.setOnClickListener(View.OnClickListener {
            categoryDialog.dismiss()
        })


    }

    private fun addCat(name:String){

        db.collection(Constants.CATEGORIES).
        document(Constants.CATEGORIES_LIST).
        update("categoriesList", FieldValue.arrayUnion(name))
            .addOnSuccessListener {requireContext().toast("Item added")
                catsList.add(name)
            updateAdapter.notifyDataSetChanged()
            }
    }

    private fun deleteCat(name:String){

        db.collection(Constants.CATEGORIES).
        document(Constants.CATEGORIES_LIST).
        update("categoriesList", FieldValue.arrayRemove(name))
            .addOnSuccessListener {requireContext().toast("Item deleted")
                catsList.removeAt(itemPosition)
                updateAdapter.notifyItemRemoved(itemPosition)
            }
    }

    private fun updateCat(name:String){


        db.collection(Constants.CATEGORIES).
        document(Constants.CATEGORIES_LIST).
        update("categoriesList", FieldValue.arrayUnion(name))
            .addOnSuccessListener {
                requireContext().toast("Item updated")
                catsList.set(itemPosition,name)
                updateAdapter.notifyItemChanged(itemPosition)
            }


        db.collection(Constants.CATEGORIES).
        document(Constants.CATEGORIES_LIST).
        update("categoriesList",
            FieldValue.arrayRemove(catsList.get(itemPosition)))


    }


    private fun showCatDialog(type:Int){

        actionType=type

        if(actionType==UPDATE){
            categoryDialog.item_name_box.setText(catsList.get(itemPosition))
            categoryDialog.name_dialog_title.text=getString(R.string.update_category)
            categoryDialog.addNameBtn.text=getString(R.string.update)
        }
        else
        {
            categoryDialog.item_name_box.setText("")
            categoryDialog.name_dialog_title.text=getString(R.string.add_category)
            categoryDialog.addNameBtn.text=getString(R.string.add)
        }

        categoryDialog.show()
    }

}
