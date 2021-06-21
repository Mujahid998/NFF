package fragments



import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nff.R
import kotlinx.android.synthetic.main.fragment_checkout.*
import modelClasses.OrderItem
import modelClasses.ReceiptModel
import modelClasses.SellingItems
import utils.Constants
import utils.HelperMethods.Companion.toast
import utils.PdfHelper
import viewModels.SharedViewModel
import workManager.EmailWorker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CheckOutFragment : BaseFragment(){

    private val args:CheckOutFragmentArgs by navArgs()

    private val sharedData: SharedViewModel by activityViewModels()
    private lateinit var itemsList: MutableList<SellingItems>
    private  var receiptList: MutableList<ReceiptModel> = ArrayList()
    private val db = Firebase.firestore
    private var afterDiscountPrice:Float=0F
    private var discount:Float=0F
    private var orderPrice:Float=0F
    private var pantryItems:Int=0
    private lateinit var file:File
    private lateinit var fileWithPrice:File
    private lateinit var orderNumber:String



    override fun onCreateView(
            inflater:
            LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {



        afterDiscountPrice=args.afterDiscountPrice
        orderPrice=args.price
        discount=args.discount
        pantryItems=args.pantry


        return getPersistentView(
                inflater,
                container,
                savedInstanceState,
                R.layout.fragment_checkout
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true



            initViews()

        }


        sharedData.getOrderListLive().observe(viewLifecycleOwner, Observer {
            itemsList = it

            val dummyList: MutableList<SellingItems> = ArrayList()


            it.forEach {

                val receiptModel = ReceiptModel()
                receiptModel.quantity = it.orderCounts.toString()
                receiptModel.name = it.name
                receiptModel.size = it.sellingItemsSizesList.get(it.selectedSizePosition).size
                receiptModel.note = it.note

                if(it.orderCounts>0){ receiptList.add(receiptModel) }

            }


        })



    }


    private fun initViews(){

        val navController=findNavController()

        back_btn_checkOut.setOnClickListener {

            navController.navigateUp()
        }


        confirmBtn.setOnClickListener {

            if(receiptList.isEmpty()){
                requireContext().toast("No item found in order")
            }
            else
            {
                confirmOder()
            }
        }
    }




    private fun confirmOder(){

        val name = customerName.text.toString()
        val email = customerEmail.text.toString()


        if (name.isEmpty()) {
            requireContext().toast("Enter customer name")
        }
        else
        {
            generatePdf(name, email)
        }
    }

    private fun generatePdf(name: String, email: String){


        val millis=System.currentTimeMillis().toString()
        orderNumber=millis.substring(millis.length - 6)

        file= PdfHelper.generatePDF(
                getDate(),
                pantryItems.toString(),
                null,
                null,
                name,
                orderNumber,
                receiptList,
                requireContext())

        fileWithPrice= PdfHelper.generatePDF(
                getDate(),
                pantryItems.toString(),
                orderPrice.toString(),
                afterDiscountPrice.toString(),
                name,
                orderNumber,
                receiptList,
                requireContext())

        submitData(name,email)

    }


    private fun submitData(name: String, email: String){


        val item=OrderItem()
        item.customerName=name
        item.date=getDate()
        item.customerEmail=email
        item.discount=discount
        item.orderPrice=orderPrice
        item.orderNumber=orderNumber
        item.sellingItemsList=itemsList


        val dbRef=db.collection(Constants.ORDERS).document(orderNumber)

        dbRef.set(item)

        sendEmail(email,name)
    }

    private fun sendEmail(email: String,name: String){

        Log.v("NFFEmail", "sendEmail")

        try {
            val emailData = workDataOf(
                    Constants.KEY_ATTACH_FILE to file.absolutePath,
                    Constants.KEY_ATTACH_FILE_WITH_PRICE to fileWithPrice.absolutePath,
                    Constants.KEY_CUSTOMER_EMAIL to email,
                    Constants.KEY_CUSTOMER_NAME to name)

            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val uploadWorkRequest = OneTimeWorkRequestBuilder<EmailWorker>()
                    .setInputData(emailData)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest)

            requireContext().toast("Order confirmed")

            findNavController().navigateUp()
        }
        catch (e: Exception) {
            requireContext().toast(e.message.toString())

        }




    }


    private fun getDate(): String{

        return SimpleDateFormat(
                "MM-dd-yyyy",
                Locale.getDefault()
        ).format(Date())

    }


}
