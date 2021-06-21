package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nff.R
import kotlinx.android.synthetic.main.list_item_categories.view.cat_name
import kotlinx.android.synthetic.main.list_item_update.view.*

class UpdateAdapter(val items:Boolean,context: Context, var list: List<String>) :
    RecyclerView.Adapter<UpdateAdapter.UpdateHolder>() {

    private val catsList: List<String> = list
    private val mContext: Context = context
    private lateinit var clickListener: ClickListener


    inner class UpdateHolder (v: View) : RecyclerView.ViewHolder(v),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.edit_btn.setOnClickListener(this)
            itemView.delete_btn.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            clickListener.onDataItemClick(adapterPosition, v)
        }


    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onDataItemClick(position: Int, v: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_update, parent, false)
        return UpdateHolder(itemView)
    }

    override fun onBindViewHolder(holder: UpdateHolder, position: Int) {

        if(items){holder.itemView.nav_btn.visibility=View.GONE}

        val cat=catsList[position]

        holder.itemView.cat_name.text=cat


    }

    override fun getItemCount(): Int {
        return catsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }




}
