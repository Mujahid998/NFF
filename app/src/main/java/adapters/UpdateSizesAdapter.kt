package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nff.R
import kotlinx.android.synthetic.main.list_item_sizes.view.size_name
import kotlinx.android.synthetic.main.list_item_sizes_update.view.*
import modelClasses.FoodItemsSizes

class UpdateSizesAdapter(context: Context, var list: List<FoodItemsSizes>) :
    RecyclerView.Adapter<UpdateSizesAdapter.SizeHolder>() {

    private val sizesListSelling: List<FoodItemsSizes> = list
    private val mContext: Context = context
    private lateinit var clickListener: ClickListener


    inner class SizeHolder (v: View) : RecyclerView.ViewHolder(v),
        View.OnClickListener {

        init {
            itemView.editItem.setOnClickListener(this)
            itemView.delete_btn.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onSizeClick(adapterPosition, v)
        }


    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onSizeClick(position: Int, v: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_sizes_update, parent, false)
        return SizeHolder(itemView)
    }

    override fun onBindViewHolder(holder: SizeHolder, position: Int) {

        val sizeItem=sizesListSelling[position]

        holder.itemView.size_name.text=sizeItem.size
        holder.itemView.size_points.text=sizeItem.points.toString()


    }

    override fun getItemCount(): Int {
        return sizesListSelling.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }




}
