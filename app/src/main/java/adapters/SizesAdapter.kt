package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nff.R
import kotlinx.android.synthetic.main.list_item_sizes.view.*
import modelClasses.SellingItemsSizes

class SizesAdapter(context: Context, var list: List<SellingItemsSizes>) :
    RecyclerView.Adapter<SizesAdapter.SizesHolder>() {

    private val sizesListSelling: List<SellingItemsSizes> = list
    private val mContext: Context = context
    private lateinit var clickListener: ClickListener


    inner class SizesHolder (v: View) : RecyclerView.ViewHolder(v),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onSizeClick(adapterPosition, v)
        }


    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onSizeClick(position: Int, v: View?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_sizes, parent, false)
        return SizesHolder(itemView)
    }

    override fun onBindViewHolder(holder: SizesHolder, position: Int) {

        val sizeItem=sizesListSelling[position]

        holder.itemView.size_name.text=sizeItem.size

        if(sizeItem.isSelected){

            holder.itemView.size_name.setBackgroundResource(R.drawable.bg_button_primary)
            holder.itemView.size_name.setTextColor(ContextCompat.getColor(mContext,R.color.white))
        }
        else{
            holder.itemView.size_name.setBackgroundResource(R.drawable.bg_holo_gray_lite)
            holder.itemView.size_name.setTextColor(ContextCompat.getColor(mContext,R.color.colorGray))

        }


    }

    override fun getItemCount(): Int {
        return sizesListSelling.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }




}
