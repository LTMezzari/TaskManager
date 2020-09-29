package mezzari.torres.lucas.taskmanager.flow.main.task

import androidx.lifecycle.*
import io.objectbox.Box
import kotlinx.coroutines.launch
import mezzari.torres.lucas.taskmanager.di.dispatcher.IAppDispatcher
import mezzari.torres.lucas.taskmanager.model.*
import mezzari.torres.lucas.taskmanager.repository.IBoxManager

/**
 * @author Lucas T. Mezzari
 * @since 17/09/2020
 */
class TaskViewModel(
    private val dispatcher: IAppDispatcher,
    repository: IBoxManager
) : ViewModel() {

    private val columnsBox: Box<Column> = repository.getBox(Column::class)
    private val tasksBox: Box<Task> = repository.getBox(Task::class)

    var board: Board? = null
    var task: Task? = null
        set(value) {
            field = value
            field?.also {
                title.value = it.title
                description.value = it.description
                isHindered.value = it.isHindered
//                _column.value = it.column
//                _parent.value = it.parent
            }
        }

    val title: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()

    private val _column: MutableLiveData<Column> = MutableLiveData()
    val columnIndex: LiveData<Int> = Transformations.map(_column) {
        if (it == null || columns.value == null)
            return@map 0
        return@map columns.value?.indexOf(it) ?: 0
    }

    fun setColumn(column: Column?) {
        _column.value = column
    }

    private val _parent: MutableLiveData<Task> = MutableLiveData()
    val parentIndex: LiveData<Int> = Transformations.map(_parent) {
        if (it == null || tasks.value == null)
            return@map 0
        return@map tasks.value?.indexOf(it) ?: 0
    }

    fun setParent(parent: Task?) {
        _parent.value = parent
    }

    val isHindered: MutableLiveData<Boolean> = MutableLiveData()

    val columns: MutableLiveData<List<Column>> = MutableLiveData()
    val tasks: MutableLiveData<List<Task>> = MutableLiveData()

    val isFormValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val observer: (Any) -> Unit = {
            val title = title.value
            val column = _column.value
            value = (!title.isNullOrEmpty() && title.isNotBlank())
                    && column != null
        }

        addSource(title, observer)
        addSource(_column, observer)
    }

    fun createTask(callback: (Task?) -> Unit) {
        val title = title.value ?: return
        val columnId = _column.value?.id ?: return
        val boardId = board?.id ?: return
        val isHindered = isHindered.value ?: false
        val description = description.value
        val task = _parent.value?.id

        if (isFormValid.value != true) {
            callback(null)
            return
        }

        val created = createTask(title, columnId, boardId, isHindered, description, task)
        tasksBox.put(created)
        callback(created)
    }

    private fun createTask(
        title: String,
        columnId: Long,
        boardId: Long,
        isHindered: Boolean,
        description: String?,
        parentId: Long?
    ): Task {
        if (task != null) {
            val updatedTask = task!!
            updatedTask.columnId = columnId
            updatedTask.boardId = boardId
            updatedTask.title = title
            updatedTask.description = description
            updatedTask.parentId = parentId
            updatedTask.isHindered = isHindered
            return updatedTask
        }

        return Task(0, columnId, boardId, title, description, parentId, isHindered = isHindered)
    }

    fun loadColumns() {
        val boardId = board?.id ?: return
        viewModelScope.launch(dispatcher.io) {
            columns.postValue(columnsBox
                .query()
                .equal(Column_.boardId, boardId)
                .order(Column_.position)
                .build()
                .find()
            )
        }
    }

    fun loadTasks() {
        val boardId = board?.id ?: return
        viewModelScope.launch(dispatcher.io) {
            tasks.postValue(tasksBox
                .query()
                .equal(Task_.boardId, boardId)
                .order(Task_.position)
                .build()
                .find()
            )
        }
    }
}