package mezzari.torres.lucas.taskmanager.flow.main.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import mezzari.torres.lucas.conductor.source.generic.implementation.BaseFragment
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.adapter.GenericAdapter
import mezzari.torres.lucas.taskmanager.databinding.FragmentTaskBinding
import mezzari.torres.lucas.taskmanager.flow.MainConductor
import mezzari.torres.lucas.taskmanager.model.Column
import mezzari.torres.lucas.taskmanager.model.Task
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
class TaskFragment: BaseFragment() {
    override val conductor: MainConductor by inject()

    val viewModel: TaskViewModel by viewModel()

    private lateinit var binding: FragmentTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val columnsAdapter = GenericAdapter<Column>(requireContext(), "Select a Column")
        val tasksAdapter = GenericAdapter<Task>(requireContext(), "Select a Parent")

        viewModel.isFormValid.observe(viewLifecycleOwner) {
            binding.btnCreate.isEnabled = it ?: false
        }

        viewModel.columns.observe(viewLifecycleOwner) {
            columnsAdapter.items = it ?: arrayListOf()
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.items = it ?: arrayListOf()
        }

        binding.apply {
            spColumn.adapter = columnsAdapter
            spParent.adapter = tasksAdapter

            spColumn.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val item = columnsAdapter.getItem(position) ?: return
                    viewModel?.setColumn(item)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

            spParent.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val item = tasksAdapter.getItem(position) ?: return
                    viewModel?.setParent(item)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

            btnCreate.setOnClickListener {
                viewModel?.createTask {
                    if (it == null)
                        return@createTask
                    next()
                }
            }
        }

        viewModel.loadColumns()
        viewModel.loadTasks()
    }
}