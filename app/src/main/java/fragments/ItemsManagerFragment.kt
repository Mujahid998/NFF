package fragments



import adapters.UpdateAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nff.R
import kotlinx.android.synthetic.main.dialog_add_note.*
import kotlinx.android.synthetic.main.dialog_delete_confirmation.*
import kotlinx.android.synthetic.main.fragment_archive.*
import kotlinx.android.synthetic.main.fragment_data_manager.*
import kotlinx.android.synthetic.main.fragment_data_manager.categories_name
import modelClasses.FoodItems
import modelClasses.SellingItems
import utils.Constants
import utils.HelperMethods.Companion.toast
import viewModels.ItemsViewModel
import viewModels.ItemsViewModelFactory
import viewModels.SellingItemsViewModel
import viewModels.SellingItemsViewModelFactory


class ItemsManagerFragment : BaseFragment(), UpdateAdapter.ClickListener {


    private val args: ItemsManagerFragmentArgs by navArgs()


    private lateinit var itemsViewModel: ItemsViewModel
    private lateinit var updateAdapter: UpdateAdapter
    private var itemslist: MutableList<String> = ArrayList()
    private var foodItemsList: MutableList<FoodItems> = ArrayList()
    private lateinit var categoryDialog: Dialog
    private var itemPosition:Int=0
    private lateinit var category:String

    private lateinit var confirmationDialog: Dialog
    private val db = Firebase.firestore



    override fun onCreateView(
        inflater:
        LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        category=args.categoryType


        itemsViewModel = ViewModelProvider(
                this,
                ItemsViewModelFactory(requireActivity().application, category)
        ).get(
                ItemsViewModel::class.java
        )

        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_data_manager)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initViewModel()
            initViews()
            initConfirmationDialog()
        }


    }


    private fun initViews(){

        val title="Manage $category items"
        categories_name.text=title

        val navController=findNavController()

        back_btn.setOnClickListener { navController.navigateUp() }

        add_btn.setOnClickListener {
            navigateToAdd(false)
        }


    }

    private fun initViewModel(){

        itemsViewModel.getItems().observe(viewLifecycleOwner,
            Observer<MutableList<FoodItems>> { it ->

                foodItemsList=it

                it.forEach {
                    itemslist.add(it.name!!)
                }

                initAdapter(itemslist)


            })
    }



    private fun initAdapter(catsList: List<String>){


        updateAdapter= UpdateAdapter(true,requireContext(),catsList)

        update_recyclerView.apply {
            adapter=updateAdapter
            setHasFixedSize(true)

        }

        updateAdapter.setOnItemClickListener(this)

        if(catsList.isEmpty()){updateMessage(true)}
    }


    override fun onDataItemClick(position: Int, v: View) {


        itemPosition=position

        when(v.id) {

            R.id.edit_btn -> {

                navigateToUpdate(itemPosition,true)
            }
            R.id.delete_btn -> {

                confirmationDialog.show()
            }


        }


    }


    private fun navigateToUpdate(position: Int,isUpdate:Boolean){

        val navController=findNavController()

        val directions=ItemsManagerFragmentDirections.toUpdateItem(
                isUpdate,category,foodItemsList.get(position)
        )

        navController.navigate(directions)
    }


    private fun navigateToAdd(isUpdate:Boolean){

        val navController=findNavController()

        val directions=ItemsManagerFragmentDirections.toUpdateItem(
                isUpdate,category
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

            deleteItem(foodItemsList.get(itemPosition))
            confirmationDialog.dismiss()
        })

        confirmationDialog.cancelDelete.setOnClickListener(View.OnClickListener {
            confirmationDialog.dismiss()
        })


    }


    private fun deleteItem(item: FoodItems){

        db.collection(Constants.ITEMS).
        document(item.id!!).delete().addOnSuccessListener {
            requireContext().toast("Item deleted")
            itemslist.removeAt(itemPosition)
            updateAdapter.notifyItemRemoved(itemPosition)
        }

    }


    private fun updateMessage(showMessage:Boolean){

        if(showMessage){no_item.visibility=View.VISIBLE}

    }




}
