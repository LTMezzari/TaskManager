package mezzari.torres.lucas.taskmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * @author Lucas T. Mezzari
 * @since 19/09/2020
 */
class GenericAdapter<T>(context: Context, hint: String? = null): BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    var items: List<T> = arrayListOf()
        set(value) {
            field = ArrayList(value)
            notifyDataSetChanged()
        }
    var hint: String? = hint
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val hasHint: Boolean get() = hint != null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, getItemViewType(position), parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, getItemViewType(position), parent)
    }

    override fun getItem(position: Int): T? {
        if (getItemViewType(position) != VIEW_TYPE_ROW) {
            return null
        }
        return items[getRealPosition(position)]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        //Get the filtered items size
        var itemCount = items.size
        //Check if there is a hint set
        if (hasHint){
            //Increment the count
            itemCount++
        }
        //Return the counter
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            hasHint && position == 0 -> {
                VIEW_TYPE_HINT
            }
            else -> {
                VIEW_TYPE_ROW
            }
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return getItemViewType(position) == VIEW_TYPE_ROW
    }

    private fun getRealPosition(position: Int): Int {
        var realPosition = position
        if (hasHint) {
            realPosition--
        }

        return realPosition
    }

    private fun createView(position: Int, viewType: Int, parent: ViewGroup?): View {
        when (viewType) {
            VIEW_TYPE_HINT -> {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = hint
                return view
            }

            else -> {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                val item = items[getRealPosition(position)]
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = item?.toString()
                return view
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_HINT = 0
        private const val VIEW_TYPE_ROW = 1
    }
}