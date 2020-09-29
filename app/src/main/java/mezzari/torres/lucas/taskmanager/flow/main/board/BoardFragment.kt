package mezzari.torres.lucas.taskmanager.flow.main.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mezzari.torres.lucas.conductor.source.generic.implementation.BaseFragment
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.adapter.TaskAdapter
import mezzari.torres.lucas.taskmanager.databinding.FragmentBoardBinding
import mezzari.torres.lucas.taskmanager.flow.MainConductor
import mezzari.torres.lucas.taskmanager.flow.dialog.column.CreateColumnDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
class BoardFragment : BaseFragment() {
    override val conductor: MainConductor by inject()

    val viewModel: BoardViewModel by viewModel()

    private lateinit var binding: FragmentBoardBinding

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {
                private var fromViewHolder: RecyclerView.ViewHolder? = null

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = .5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1f

                    val from = fromViewHolder ?: return

                    adapter.finishMoveItem(from)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    fromViewHolder = viewHolder
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    adapter.moveItem(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    private val adapter: TaskAdapter by lazy {
        TaskAdapter(requireContext()).apply {
            onEmptyBoardClickListener = {
                openColumnDialog()
            }

            onEmptyColumnClickListener = {
                next()
            }

            onTaskClickListener = {
                conductor.task = it
                next()
            }

            onStartDragClickListener = {
                itemTouchHelper.startDrag(it)
            }

            onChangeColumnPositions = { column1, column2 ->
                viewModel.changeColumnPosition(column1, column2)
            }

            onChangeTaskPositions = { _, task1, task2 ->
                viewModel.changeTaskPosition(task1, task2)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false)
        binding.viewModel = viewModel
        binding.isFabExpanded = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            itemTouchHelper.attachToRecyclerView(rvTasks)
            rvTasks.also {
                it.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                it.adapter = adapter
            }

            btnAdd.setOnClickListener {
                isFabExpanded = !isFabExpanded
            }

            btnAddColumn.setOnClickListener {
                openColumnDialog()
            }

            btnAddTask.setOnClickListener {
                next()
            }
        }

        viewModel.columns.observe(viewLifecycleOwner) {
            adapter.setColumns(it ?: arrayListOf())
        }
    }

    private fun openColumnDialog() {
        val board = viewModel.board ?: return
        CreateColumnDialog.show(requireActivity() as AppCompatActivity, board) {
            viewModel.loadColumns()
        }
    }
}