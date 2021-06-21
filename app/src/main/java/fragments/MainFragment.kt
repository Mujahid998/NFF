package fragments

import adapters.ItemsAdapter
import adapters.SizesAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nff.R
import kotlinx.android.synthetic.main.bottom_sheet_item_sizes.*
import kotlinx.android.synthetic.main.dialog_add_note.*
import kotlinx.android.synthetic.main.fragment_main.*
import modelClasses.SellingItems
import modelClasses.SellingItemsSizes
import utils.HelperMethods.Companion.toast
import viewModels.SellingItemsViewModel
import viewModels.SellingItemsViewModelFactory
import viewModels.SharedViewModel


class MainFragment : BaseFragment(), ItemsAdapter.ItemAdapterListener,SizesAdapter.ClickListener {

    private val args:MainFragmentArgs by navArgs()

    private lateinit var itemsViewModel:SellingItemsViewModel
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var sizesAdapter: SizesAdapter
    private lateinit var category:String
    private lateinit var itemsList: MutableList<SellingItems>
    private lateinit var orderItemsList: MutableList<SellingItems>
    private lateinit var mSizesList: MutableList<SellingItemsSizes>
    private lateinit var noteDialog: Dialog
    private lateinit var sizesDialog: BottomSheetDialog
    private var itemPosition:Int=0
    private lateinit var navController: NavController
    private val sharedData: SharedViewModel by activityViewModels()
    private var newPoints: Float=0F
    private var totalPoints: Float=0F
    private var containsPosition=-1

    override fun onCreateView(
            inflater:
            LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        category=args.category


        itemsViewModel = ViewModelProvider(
                this,
                SellingItemsViewModelFactory(requireActivity().application, category)
        ).get(
                SellingItemsViewModel::class.java
        )

        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_main)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initNoteDialog()
            initSizesDialog()
            initViews()
        }

        sharedData.getOrderListLive().observe(viewLifecycleOwner, { orderItemsList ->
            this.orderItemsList=orderItemsList
            updatePoints(orderItemsList)
        })


        itemsViewModel.getItems().observe(viewLifecycleOwner,
                Observer<MutableList<SellingItems>> { itemsList ->



                    sharedData.getOrderList().forEach {

                        for(position in 0 until itemsList.size){

                            val item=itemsList.get(position)

                            sharedData.getOrderList().forEach {

                                if(item.equals(it)){

                                    itemsList.set(position,it)

                                }
                            }

                        }

                    }


                    initAdapter(itemsList)


                })








    }


    private fun initViews(){

        navController=findNavController()
        val navigateToOrder=MainFragmentDirections.mainToOrder()
        val navigateToArchive=MainFragmentDirections.mainToArchive()

        categories_name.text=category

        categories_btn.setOnClickListener {
          navController.navigateUp()
        }

        shop_btn.setOnClickListener {
           navController.navigate(navigateToOrder)
        }

        archive_btn.setOnClickListener {
            navController.navigate(navigateToArchive)
        }

    }



    private fun orderListContains(item:SellingItems):Boolean{

        var contains=false

        for (position in 0 until orderItemsList.size) {

            val orderItem = orderItemsList.get(position)

            if(item.equals(orderItem)){
                containsPosition=position
                contains=true
            }

        }

        return contains
    }


    private fun initAdapter(itemsList: MutableList<SellingItems>){


        this.itemsList=itemsList
        itemsAdapter= ItemsAdapter(false,requireContext(), itemsList)
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

        items_recyclerView.apply {
            adapter=itemsAdapter
        }

        itemsAdapter.setOnItemClickListener(this)
    }


    override fun onItemClick(position: Int, v: View) {

        this.itemPosition=position

        when(v.id){

            R.id.add_item -> {
                updateOrderCount(position, 1)
            }
            R.id.remove_item -> {
                updateOrderCount(position, -1)
            }
            R.id.copy_item -> {
                copyItem(position)
            }
            R.id.add_note -> {
                noteDialog.note_box.setText(itemsList.get(itemPosition).note)
                noteDialog.show()
            }
            R.id.sizes_btn -> {

                val sizesList = itemsList.get(position).sellingItemsSizesList
                if (sizesList.size > 1) {
                    showSizesDialog(sizesList)
                }

            }
        }
    }

    override fun onOrderCountChange(position: Int, count: String) {


        val item=itemsList.get(position)
        item.orderCounts= count.toInt()
        if(item.orderCounts!! <0){item.orderCounts=0}
        itemsList.set(position, item)

        updateSharedList(item)
    }


    private fun updateOrderCount(position: Int, value: Int){

        val item=itemsList.get(position)
        item.orderCounts= item.orderCounts?.plus(value)
        if(item.orderCounts!! <0){item.orderCounts=0}
        itemsList.set(position, item)
        itemsAdapter.notifyItemChanged(position)


    }

    private fun copyItem(position: Int){

        val item=itemsList.get(position)
        val newItem=SellingItems()
        newItem.id=System.currentTimeMillis().toString()
        newItem.name=item.name
        newItem.category=item.category
        newItem.orderCounts=item.orderCounts
        newItem.isFree=item.isFree
        if(newItem.orderCounts==0){newItem.orderCounts=1}
        newItem.note=item.note
        newItem.sellingItemsSizesList=item.sellingItemsSizesList
        newItem.selectedSizePosition=item.selectedSizePosition
        itemsList.add(position + 1, newItem)

        itemsAdapter.notifyItemInserted(position + 1)

        sharedData.insertNewItem(newItem)

    }


    private fun addNote(position: Int, value: String){

        val item=itemsList.get(position)
        item.note= value
        itemsList.set(position, item)
        requireContext().toast("Note added")
        itemsAdapter.notifyItemChanged(position)

    }


    private fun initNoteDialog() {

        noteDialog = Dialog(requireContext())
        noteDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        noteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        noteDialog.setContentView(R.layout.dialog_add_note)
        noteDialog.setCancelable(false)

        noteDialog.addNoteBtn.setOnClickListener(View.OnClickListener {

            val noteText = noteDialog.note_box.text.toString()

            if (noteText.isEmpty()) {
                requireContext().toast("Write some note")
            } else {
                noteDialog.dismiss()
                addNote(itemPosition, noteText)
            }

        })

        noteDialog.cancelNoteBtn.setOnClickListener(View.OnClickListener {
            noteDialog.dismiss()
        })


    }


    private fun initSizesDialog() {

        sizesDialog = BottomSheetDialog(requireContext())

        val inflater = LayoutInflater.from(requireContext())
        val sheetView = inflater.inflate(R.layout.bottom_sheet_item_sizes,
                requireActivity().window.decorView.rootView as ViewGroup, false)

        sizesDialog.setContentView(sheetView)


        val mBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(sheetView.parent as View)

        sizesDialog.setOnShowListener {
            mBehavior.setPeekHeight(sheetView.height)
        }

    }


    private fun showSizesDialog(sizesList: MutableList<SellingItemsSizes>){

        mSizesList=sizesList

        sizesAdapter= SizesAdapter(requireContext(), mSizesList)

        sizesDialog.sizes_recyclerView.apply {
            adapter=sizesAdapter
        }

        sizesAdapter.setOnItemClickListener(this)

        sizesDialog.show()

    }

    override fun onSizeClick(position: Int, v: View?) {

        val sizeItem=mSizesList.get(position)

        if(!sizeItem.isSelected&&mSizesList.size>1){

            val  lastPosition=itemsList.get(itemPosition).selectedSizePosition
            val lastSizeItem=mSizesList.get(lastPosition)
            lastSizeItem.isSelected=false
            mSizesList.set(lastPosition, lastSizeItem)
            sizeItem.isSelected=true
            mSizesList.set(position, sizeItem)

            val item=itemsList.get(itemPosition)
            item.selectedSizePosition=position
            itemsList.set(itemPosition, item)

            sizesAdapter.notifyDataSetChanged()
            itemsAdapter.notifyItemChanged(itemPosition)

            updateSharedList(item)
        }
    }

    private fun updateSharedList(item: SellingItems){

        if (orderListContains(item)) {
            sharedData.updateItem(containsPosition, item)
        } else {
            sharedData.insertNewItem(item)
        }

    }

    private fun updatePoints(itemsList: MutableList<SellingItems>){

        Log.v("OrderList", "updatePoints " )

        newPoints=0F

        itemsList.forEach { item ->

            if(item.orderCounts!! >0) {

                val sizesList = item.sellingItemsSizesList
                val sizePosition = item.selectedSizePosition
                val size = sizesList[sizePosition]

                val points = size.points * item.orderCounts

                newPoints += points
            }

        }


        shop_btn.text=(newPoints).toString()


    }


}
