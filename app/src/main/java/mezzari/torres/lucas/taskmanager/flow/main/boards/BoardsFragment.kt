package mezzari.torres.lucas.taskmanager.flow.main.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import mezzari.torres.lucas.conductor.source.generic.implementation.BaseFragment
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.adapter.BoardAdapter
import mezzari.torres.lucas.taskmanager.databinding.FragmentBoardsBinding
import mezzari.torres.lucas.taskmanager.flow.MainConductor
import mezzari.torres.lucas.taskmanager.flow.dialog.board.CreateBoardDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BoardsFragment : BaseFragment() {
    override val conductor: MainConductor by inject()
    val viewModel: BoardsViewModel by viewModel()

    private lateinit var binding: FragmentBoardsBinding

    private val adapter: BoardAdapter by lazy {
        BoardAdapter(requireContext()).apply {
            onBoardClick = {
                conductor.board = it
                next()
            }

            onAddBoardClick = {
                openBoardDialog()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_boards, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            rvBoards.also {
                it.adapter = adapter
                it.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            }

            btnAdd.setOnClickListener {
                openBoardDialog()
            }
        }

        viewModel.boards.observe(viewLifecycleOwner) {
            adapter.boards = it ?: arrayListOf()
            binding.tvBoardCount.text = String.format(getString(R.string.format_board_count), it?.size ?: 0)
        }
    }

    private fun openBoardDialog() {
        CreateBoardDialog.show(requireActivity() as AppCompatActivity) {
            viewModel.loadBoards()
        }
    }
}