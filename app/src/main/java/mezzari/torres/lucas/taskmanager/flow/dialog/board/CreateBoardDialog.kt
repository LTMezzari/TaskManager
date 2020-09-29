package mezzari.torres.lucas.taskmanager.flow.dialog.board

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.dialog_create_board.*
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.databinding.DialogCreateBoardBinding
import mezzari.torres.lucas.taskmanager.generic.BaseDialog
import mezzari.torres.lucas.taskmanager.model.Board
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class CreateBoardDialog: BaseDialog() {
    private lateinit var binding: DialogCreateBoardBinding

    private val viewModel: CreateBoardViewModel by lazy {
        getViewModel<CreateBoardViewModel>(CreateBoardViewModel::class)
    }

    private var onBoardCreated: ((Board) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_board, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.board.observe(viewLifecycleOwner) {
            onBoardCreated?.invoke(it)
            dismiss()
        }

        viewModel.isFormValid.observe(viewLifecycleOwner) {
            btnCreate.isEnabled = it == true
        }

        binding.apply {
            btnCreate.setOnClickListener {
                viewModel?.createBoard()
            }
        }
    }

    companion object {
        fun show(context: AppCompatActivity, onBoardCreated: (Board) -> Unit) {
            CreateBoardDialog().apply {
                this.onBoardCreated = onBoardCreated
            }.show(context.supportFragmentManager, CreateBoardDialog::class.java.name)
        }
    }
}