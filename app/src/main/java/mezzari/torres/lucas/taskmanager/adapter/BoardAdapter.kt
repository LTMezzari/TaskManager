package mezzari.torres.lucas.taskmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_board.view.*
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.model.Board

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class BoardAdapter(context: Context): RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var items: ArrayList<Board> = ArrayList()
    var boards: List<Board> set(value) {
        items = ArrayList(value)
        notifyDataSetChanged()
    } get() = items

    var onBoardClick: ((Board) -> Unit)? = null
    var onAddBoardClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return when (viewType) {
            VIEW_TYPE_NEW_BOARD -> {
                BoardViewHolder(inflater.inflate(R.layout.row_add_board, parent, false))
            }
            else -> {
                BoardViewHolder(inflater.inflate(R.layout.row_board, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        when(getItemViewType(position)) {
            VIEW_TYPE_NEW_BOARD -> {
                holder.itemView.setOnClickListener {
                    onAddBoardClick?.invoke()
                }
            }
            else -> {
                val board = items[position]

                holder.itemView.apply {
                    tvTitle.text = board.name
                    tvDescription.also {
                        if (board.description != null) {
                            it.text = board.description
                        } else {
                            it.setText(R.string.label_no_description)
                        }
                    }

                    setOnClickListener {
                        onBoardClick?.invoke(board)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) {
            VIEW_TYPE_NEW_BOARD
        } else {
            VIEW_TYPE_BOARD
        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    class BoardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    companion object {
        private const val VIEW_TYPE_BOARD = 0
        private const val VIEW_TYPE_NEW_BOARD = 1
    }
}