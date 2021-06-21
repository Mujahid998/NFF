package adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nff.R
import kotlinx.android.synthetic.main.list_item_selling_items.view.*
import modelClasses.SellingItems

class ItemsAdapter(private val freeOption:Boolean, context: Context, list: List<SellingItems>) :
    RecyclerView.Adapter<ItemsAdapter.ItemsHolder>() {

    private val itemsList: List<SellingItems> = list
    private val mContext: Context = context
    private lateinit var mListener: ItemAdapterListener


    inner class ItemsHolder (v: View) : RecyclerView.ViewHolder(v),
            View.OnClickListener, TextWatcher {



        init {

            val countBox:EditText = itemView.findViewById(R.id.item_counts_box)

            itemView.setOnClickListener(this)
            itemView.add_item.setOnClickListener(this)
            itemView.remove_item.setOnClickListener(this)
            itemView.add_note.setOnClickListener(this)
            itemView.copy_item.setOnClickListener(this)
            itemView.sizes_btn.setOnClickListener(this)
            itemView.free_btn.setOnClickListener(this)

            itemView.item_counts_box.addTextChangedListener(this)

        }

        override fun onClick(v: View) {
            mListener.onItemClick(adapterPosition, v)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (s != null) {
                if(s.isNotEmpty())
                { mListener.onOrderCountChange(adapterPosition,s.toString())}

            }
        }

        override fun afterTextChanged(s: Editable?) {



        }



    }

    fun setOnItemClickListener(mListener: ItemAdapterListener) {
        this.mListener = mListener
    }

    interface ItemAdapterListener {
        fun onItemClick(position: Int, v: View)
        fun onOrderCountChange(position: Int,count:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_selling_items, parent, false)
        return ItemsHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemsHolder, position: Int) {

        val item=itemsList.get(position)
        val sizesList=item.sellingItemsSizesList

        if(sizesList.size==1){ holder.itemView.sizes_btn.
        setCompoundDrawablesWithIntrinsicBounds(null,null,null,null) }
        else { holder.itemView.sizes_btn.
        setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(mContext,R.drawable.ic_drop_down),null) }

        if(item.note!=null){holder.itemView.note.text=item.note
            holder.itemView.note.visibility=View.VISIBLE}
        else{holder.itemView.note.visibility=View.GONE}


        holder.itemView.item_name.text=item.name
        holder.itemView.sizes_btn.text= item.sellingItemsSizesList!!.get(item.selectedSizePosition).size

        holder.itemView.item_counts_box.setText(item.orderCounts.toString().trim())

        if(freeOption){

            if(item.isFree){
                holder.itemView.free_btn.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(mContext,R.drawable.ic_checked),null)
            }
            else
            {
                holder.itemView.free_btn.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(mContext,R.drawable.ic_uncheck),null)

            }
        }
        else
        {
            holder.itemView.free_btn.visibility=View.GONE
        }


    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }






}
