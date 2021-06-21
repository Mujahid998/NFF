package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nff.R
import kotlinx.android.synthetic.main.list_item_categories.view.*

class CategoriesAdapter(context: Context, var list: List<String>) :
    RecyclerView.Adapter<CategoriesAdapter.CatsHolder>() {

    private val catsList: List<String> = list
    private val mContext: Context = context
    private lateinit var clickListener: ClickListener


    inner class CatsHolder (v: View) : RecyclerView.ViewHolder(v),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onCategoryClick(adapterPosition, v)
        }


    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onCategoryClick(position: Int, v: View?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatsHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_categories, parent, false)
        return CatsHolder(itemView)
    }

    override fun onBindViewHolder(holder: CatsHolder, position: Int) {

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
