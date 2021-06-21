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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import com.nff.ApplicationClass
import com.nff.R
import kotlinx.android.synthetic.main.bottom_sheet_item_sizes.*
import kotlinx.android.synthetic.main.dialog_add_note.*
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.archive_btn
import kotlinx.android.synthetic.main.fragment_order.shop_btn
import modelClasses.OrderItem
import modelClasses.SellingItems
import modelClasses.SellingItemsSizes
import utils.Constants
import utils.HelperMethods.Companion.toast
import utils.OrderCalculator
import viewModels.SharedViewModel


class OrderFragment : BaseFragment(), ItemsAdapter.ItemAdapterListener,
    SizesAdapter.ClickListener {


    val args: OrderFragmentArgs by navArgs()

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var sizesAdapter: SizesAdapter
    private lateinit var category:String
    private lateinit var itemsList: MutableList<SellingItems>
    private lateinit var mSizesList: MutableList<SellingItemsSizes>
    private lateinit var noteDialog: Dialog
    private lateinit var sizesDialog: BottomSheetDialog
    private var itemPosition:Int=0
    private lateinit var navController: NavController
    private val sharedData: SharedViewModel by activityViewModels()
    private var newPoints: Float=0F
    private var afterDiscountPrice:Float=0F
    private var originalPrice:Float=0F
    private var foodsPrice:Float=0F
    private var orderDiscount:Float=0F
    private var discountedPrice:Float=0F
    private var pantryItemsCounts:Int=0
    private lateinit var orderItem:OrderItem

    override fun onCreateView(inflater:
                              LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_order)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initNoteDialog()
            initSizesDialog()
            initViews()
        }


        orderDiscount=sharedData.getDiscount()
        updateDiscount(orderDiscount)

        if(args.orderItem!=null){
            orderItem= args.orderItem!!
            orderDiscount=orderItem.discount
            orderItem.sellingItemsList?.let { initAdapter(it) }

        }
        else
        {
            sharedData.getOrderListLive().observe(viewLifecycleOwner, Observer {


                val dummyList: MutableList<SellingItems> = ArrayList()

                it.forEach { item ->
                    if (item.orderCounts <= 0) {
                        dummyList.add(item)
                    }
                }

                it.removeAll(dummyList)


                initAdapter(it)

            })

        }






    }



    private fun initViews(){


        navController=findNavController()



        val toHome=OrderFragmentDirections.orderToHome()
        val toArchive=OrderFragmentDirections.orderToArchive()

        continue_btn.setOnClickListener {

            sharedData.updateList(itemsList)
            sharedData.updateDiscount(orderDiscount)

            val toCheckOut=OrderFragmentDirections.orderToCheckOut(
                    afterDiscountPrice,orderDiscount,originalPrice,pantryItemsCounts
            )

            navController.navigate(toCheckOut)
        }

        more_btn.setOnClickListener {

            sharedData.updateList(itemsList)
            sharedData.updateDiscount(orderDiscount)

            navController.navigate(toHome)
        }

        archive_btn.setOnClickListener {
            navController.navigate(toArchive)
        }

        reorder_check.setOnCheckedChangeListener{ _, isChecked ->

           updatePrice(newPoints, isChecked)

        }


        slider.addOnChangeListener(object : Slider.OnChangeListener {

            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {

                val discountValue = "${value.toLong()}%"
                discountView.text = discountValue

                orderDiscount = (value.toLong()).toFloat()


            }



        })

        slider.post {
            slider.value=orderDiscount
        }

        slider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {

                updateDiscount(orderDiscount)
            }

        })
    }


    private fun initAdapter(itemsList: MutableList<SellingItems>){


        this.itemsList=itemsList
        itemsAdapter= ItemsAdapter(true,requireContext(), itemsList)

        orderItems_recyclerView.apply {
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
            R.id.free_btn -> {
                makeItemFree(position)
            }
            R.id.sizes_btn -> {

                val sizesList = itemsList.get(position).sellingItemsSizesList!!
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

        updatePoints()
    }

    private fun makeItemFree(position: Int) {

        val item=itemsList.get(position)
        item.isFree = !item.isFree
        itemsList.set(position, item)
        itemsAdapter.notifyItemChanged(position)

        updatePoints()
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
        newItem.note=item.note
        newItem.sellingItemsSizesList=item.sellingItemsSizesList
        newItem.selectedSizePosition=item.selectedSizePosition
        itemsList.add(position + 1, newItem)

        itemsAdapter.notifyItemInserted(position + 1)


        updatePoints()

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

            updatePoints()
        }
    }


    private fun updatePoints(){

        newPoints=0F
        pantryItemsCounts=0

        itemsList.forEach { item ->

            if(item.orderCounts>0&&!item.isFree) {

                val sizesList = item.sellingItemsSizesList
                val sizePosition = item.selectedSizePosition
                val size = sizesList[sizePosition]

                val points = size.points *item.orderCounts

                newPoints += points


                if(item.category.equals("Pantry")){
                    pantryItemsCounts+=item.orderCounts
                }
            }

        }

        shop_btn.text=(newPoints).toString()

        updatePrice(newPoints, reorder_check.isChecked)

    }


    private fun updatePrice(points: Float, reorder: Boolean){

        foodsPrice=OrderCalculator.getOrderPrice(points, reorder)


        updateDiscount(orderDiscount)
        originalPrice=foodsPrice+pantryItemsCounts * ApplicationClass.PANTRY_PRICE
    }


    private fun updateDiscount(discount: Float){

        afterDiscountPrice=foodsPrice

        discountedPrice= calculatePercentage(discount,afterDiscountPrice)

        afterDiscountPrice -= discountedPrice


        afterDiscountPrice+=pantryItemsCounts * ApplicationClass.PANTRY_PRICE


        val priceText="Price= $"+String.format("%.1f", afterDiscountPrice)

        priceView.text=priceText



    }


    fun calculatePercentage(obtained: Float, total: Float): Float {
        return (total / 100F)* obtained
        //return (obtained / total)* 100F
    }
}
