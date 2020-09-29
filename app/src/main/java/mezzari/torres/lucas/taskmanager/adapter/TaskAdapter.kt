package mezzari.torres.lucas.taskmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_column.view.*
import kotlinx.android.synthetic.main.row_task.view.*
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.model.Column
import mezzari.torres.lucas.taskmanager.model.Task

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
class TaskAdapter(private val context: Context) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var items: ArrayList<Item> = ArrayList()

    var onEmptyBoardClickListener: (() -> Unit)? = null
    var onEmptyColumnClickListener: (() -> Unit)? = null
    var onTaskClickListener: ((Task) -> Unit)? = null
    var onStartDragClickListener: ((RecyclerView.ViewHolder) -> Unit)? = null
    var onChangeColumnPositions: ((Column, Column) -> Unit)? = null
    var onChangeTaskPositions: ((Column, Task, Task?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return when (viewType) {
            VIEW_TYPE_EMPTY_BOARD -> {
                TaskViewHolder(inflater.inflate(R.layout.row_empty_board, parent, false))
            }
            VIEW_TYPE_EMPTY_COLUMN -> {
                TaskViewHolder(inflater.inflate(R.layout.row_empty_column, parent, false))
            }
            VIEW_TYPE_COLUMN -> {
                TaskViewHolder(inflater.inflate(R.layout.row_column, parent, false))
            }
            else -> {
                TaskViewHolder(inflater.inflate(R.layout.row_task, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_EMPTY_BOARD -> {
                holder.itemView.setOnClickListener {
                    onEmptyBoardClickListener?.invoke()
                }
            }

            VIEW_TYPE_EMPTY_COLUMN -> {
                holder.itemView.setOnClickListener {
                    onEmptyColumnClickListener?.invoke()
                }
            }

            VIEW_TYPE_COLUMN -> {
                val item = items[position] as ColumnItem
                holder.itemView.apply {
                    tvName.text = item.column.name

                    val ibMove = findViewById<ImageButton>(R.id.ibMove)
                    ibMove.setOnClickListener {
                        onStartDragClickListener?.invoke(holder)
                    }
                }
            }

            VIEW_TYPE_TASK -> {
                val item = items[position] as TaskItem
                holder.itemView.apply {
                    if (item.task.isHindered) {
                        setBackgroundResource(R.drawable.shape_task_hindered_card)
                        ibHindered.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@TaskAdapter.context,
                                R.drawable.ic_baseline_flag_24
                            )
                        )
                    } else {
                        setBackgroundResource(R.drawable.shape_task_card)
                        ibHindered.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@TaskAdapter.context,
                                R.drawable.ic_baseline_outlined_flag_24
                            )
                        )
                    }

                    tvTitle.text = item.task.title
                    tvDescription.also {
                        if (item.task.description != null) {
                            it.text = item.task.description
                        } else {
                            it.setText(R.string.label_no_description)
                        }
                    }

                    val ibMove = findViewById<ImageButton>(R.id.ibMove)
                    ibMove.setOnClickListener {
                        onStartDragClickListener?.invoke(holder)
                    }

                    setOnClickListener {
                        onTaskClickListener?.invoke(item.task)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) {
            1
        } else {
            items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (items.isEmpty())
            return VIEW_TYPE_EMPTY_BOARD
        return when (items[position]) {
            is EmptyItem -> VIEW_TYPE_EMPTY_COLUMN
            is ColumnItem -> VIEW_TYPE_COLUMN
            else -> VIEW_TYPE_TASK
        }
    }

    private fun extractItems(columns: List<Column>): ArrayList<Item> {
        val items = ArrayList<Item>()
        for (column in columns) {
            val children = ArrayList<Item>()
            val columnItem = ColumnItem(column, children)
            items += columnItem
            if (column.tasks.isEmpty()) {
                items += EmptyItem(column)
                continue
            }

            for (task in column.tasks) {
                val taskItem = TaskItem(column, task, columnItem)
                items += taskItem
                children += taskItem
            }
        }
        return items
    }

    fun setColumns(columns: List<Column>) {
        this.items = extractItems(columns)
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        val fromItem = items[from]
        val toItem = items[to]

        items[to] = fromItem
        items[from] = toItem
        notifyItemMoved(from, to)
    }

    fun finishMoveItem(viewHolder: RecyclerView.ViewHolder) {
        val from = viewHolder.adapterPosition
        val to = if (from == 0) from + 1 else from - 1

        val fromItem = items[from]
        val toItem = items[to]

        if (toItem is ColumnItem) {
            if (fromItem is ColumnItem) {
                fromItem.column.position = from
                toItem.column.position = to
                onChangeColumnPositions?.invoke(fromItem.column, toItem.column)
            } else if (fromItem is TaskItem) {
                fromItem.column = toItem.column
                fromItem.task.position = from
                fromItem.task.columnId = toItem.column.id
                onChangeTaskPositions?.invoke(toItem.column, fromItem.task, null)
            }
        } else if (toItem is TaskItem) {
            if (fromItem is TaskItem) {
                fromItem.column = toItem.column
                fromItem.task.position = from
                fromItem.task.columnId = toItem.column.id
                toItem.task.position = to
                onChangeTaskPositions?.invoke(toItem.column, fromItem.task, toItem.task)
            } else if (fromItem is ColumnItem) {
                toItem.column = fromItem.column
                toItem.task.position = to
                toItem.task.columnId = fromItem.column.id
                fromItem.column.position = from
            }
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private abstract class Item(
        var column: Column
    )

    private class ColumnItem(
        column: Column,
        val children: List<Item>
    ) : Item(column)

    private class EmptyItem(
        column: Column
    ) : Item(column)

    private class TaskItem(
        column: Column,
        val task: Task,
        val parent: ColumnItem
    ) : Item(column)

    companion object {
        private const val VIEW_TYPE_COLUMN = 0
        private const val VIEW_TYPE_TASK = 1
        private const val VIEW_TYPE_EMPTY_BOARD = 2
        private const val VIEW_TYPE_EMPTY_COLUMN = 3
    }
}