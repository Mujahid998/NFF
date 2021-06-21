package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nff.R
import kotlinx.android.synthetic.main.list_item_archived_items.view.*
import modelClasses.OrderItem

class ArchivedAdapter(context: Context, list: List<OrderItem>) :
    RecyclerView.Adapter<ArchivedAdapter.ItemsHolder>() {

    private val ordersList: List<OrderItem> = list
    private val mContext: Context = context
    private lateinit var mListener: ItemClickListener


    inner class ItemsHolder (v: View) : RecyclerView.ViewHolder(v),
            View.OnClickListener {



        init {

            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            mListener.onItemClick(adapterPosition, v)
        }




    }

    fun setOnItemClickListener(mListener: ItemClickListener) {
        this.mListener = mListener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int, v: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_archived_items, parent, false)
        return ItemsHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemsHolder, position: Int) {

        val order=ordersList.get(position)

        val orderNumber="#${order.orderNumber}"
        val customer="Customer: ${order.customerName}"
        holder.itemView.orderNumber.text=orderNumber
        holder.itemView.customer.text=customer
        holder.itemView.date.text=order.date

    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }






}
