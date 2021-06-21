package fragments



import adapters.ArchivedAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nff.R
import kotlinx.android.synthetic.main.fragment_archive.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.cats_recyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import modelClasses.OrderItem
import viewModels.OrdersViewModel
import java.util.*
import kotlin.collections.ArrayList


class ArchiveFragment : BaseFragment(), ArchivedAdapter.ItemClickListener {



    private lateinit var ordersViewModel: OrdersViewModel
    private lateinit var archivedAdapter: ArchivedAdapter
    private var ordersList: List<OrderItem> = ArrayList()
    private var searchList: MutableList<OrderItem> = ArrayList()



    override fun onCreateView(
            inflater:
            LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {



        ordersViewModel= ViewModelProvider(this).get(OrdersViewModel::class.java)

        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_archive)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initViewModel()
            initViews()
            searchOrders()
        }


    }


    private fun initViews(){

        val navController=findNavController()

        back_btn_archive.setOnClickListener { navController.navigateUp() }



    }

    private fun initViewModel(){

        ordersViewModel.getOrders().observe(viewLifecycleOwner,
                Observer<List<OrderItem>> { it ->

                    ordersList = it
                    initAdapter(ordersList)


                })
    }

    private fun searchOrders(){

        search_box.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s != null) {

                    if (s.isNotEmpty()) {

                        searchList.clear()

                        ordersList.forEach { item ->

                            if (item.customerName!!.startsWith(s.toString())
                                    ||item.customerName!!.startsWith(s.toString().capitalize(Locale.ROOT))) {

                                searchList.add(item)
                            }

                        }

                        initAdapter(searchList)
                    } else {
                        searchList.clear()
                        initAdapter(ordersList)
                    }

                } else {
                    searchList.clear()
                    initAdapter(ordersList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}


        })
    }


    private fun initAdapter(ordersList: List<OrderItem>){


        archivedAdapter= ArchivedAdapter(requireContext(), ordersList)

        cats_recyclerView.apply {
            adapter=archivedAdapter
            setHasFixedSize(true)
        }

        archivedAdapter.setOnItemClickListener(this)

        if(ordersList.isEmpty()){updateMessage(true)}
        else{updateMessage(false)}
    }



    override fun onItemClick(position: Int, v: View) {

        val navController=findNavController()

        val item: OrderItem

        if(searchList.isEmpty()){item=ordersList.get(position)}
        else{item=searchList.get(position)}

        val navigateToOrder=ArchiveFragmentDirections.archiveToOrder(item)

        navController.navigate(navigateToOrder)
    }

    private fun updateMessage(showMessage: Boolean){

        if(showMessage){empty_archive.visibility=View.VISIBLE}
        else{empty_archive.visibility=View.GONE}

    }


}
