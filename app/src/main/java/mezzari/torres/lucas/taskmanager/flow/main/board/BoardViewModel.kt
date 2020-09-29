package mezzari.torres.lucas.taskmanager.flow.main.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.objectbox.Box
import kotlinx.coroutines.launch
import mezzari.torres.lucas.taskmanager.di.dispatcher.IAppDispatcher
import mezzari.torres.lucas.taskmanager.model.*
import mezzari.torres.lucas.taskmanager.repository.IBoxManager

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
class BoardViewModel(
    private val dispatcher: IAppDispatcher,
    repository: IBoxManager
): ViewModel() {

    private val columnsBox: Box<Column> = repository.getBox(Column::class)
    private val tasksBox: Box<Task> = repository.getBox(Task::class)

    var board: Board? = null
    set(value) {
        field = value
        loadColumns()
    }

    private val _columns: MutableLiveData<List<Column>> = MutableLiveData()
    val columns: LiveData<List<Column>> get() = _columns

    fun loadColumns() {
        val board = board ?: return
        viewModelScope.launch (dispatcher.io) {
            val columns = columnsBox.query()
                .equal(Column_.boardId, board.id)
                .order(Column_.position)
                .build()
                .find()

            for (column in columns) {
                column.board = board
                val tasks = tasksBox.query()
                    .equal(Task_.columnId, column.id)
                    .order(Task_.position)
                    .build()
                    .find()
                column.tasks = tasks
            }

            _columns.postValue(columns)
        }
    }

    fun changeTaskPosition(task1: Task, task2: Task?) {
        tasksBox.put(task1)
        task2?.run {
            tasksBox.put(this)
        }
        loadColumns()
    }

    fun changeColumnPosition(column1: Column, column2: Column) {
        columnsBox.put(column1, column2)
        loadColumns()
    }
}