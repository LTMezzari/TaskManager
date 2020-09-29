package mezzari.torres.lucas.taskmanager.flow.dialog.column

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.dialog_create_board.*
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.databinding.DialogCreateColumnBinding
import mezzari.torres.lucas.taskmanager.generic.BaseDialog
import mezzari.torres.lucas.taskmanager.model.Board
import mezzari.torres.lucas.taskmanager.model.Column
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
class CreateColumnDialog: BaseDialog() {
    private lateinit var binding: DialogCreateColumnBinding

    private val viewModel: CreateColumnViewModel by lazy {
        getViewModel<CreateColumnViewModel>(CreateColumnViewModel::class)
    }

    private var onColumnCreated: ((Column) -> Unit)? = null
    private var board: Board? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_column, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.board = board

        viewModel.column.observe(viewLifecycleOwner) {
            onColumnCreated?.invoke(it)
            dismiss()
        }

        viewModel.isFormValid.observe(viewLifecycleOwner) {
            btnCreate.isEnabled = it == true
        }

        binding.apply {
            btnCreate.setOnClickListener {
                viewModel?.createColumn()
            }
        }
    }

    companion object {
        fun show(context: AppCompatActivity, board: Board, onColumnCreated: (Column) -> Unit) {
            CreateColumnDialog().apply {
                this.onColumnCreated = onColumnCreated
                this.board = board
            }.show(context.supportFragmentManager, CreateColumnDialog::class.java.name)
        }
    }
}