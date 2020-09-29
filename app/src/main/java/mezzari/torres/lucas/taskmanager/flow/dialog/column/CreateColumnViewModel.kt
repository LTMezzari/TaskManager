package mezzari.torres.lucas.taskmanager.flow.dialog.column

import androidx.lifecycle.*
import io.objectbox.Box
import kotlinx.coroutines.launch
import mezzari.torres.lucas.taskmanager.di.dispatcher.IAppDispatcher
import mezzari.torres.lucas.taskmanager.model.Board
import mezzari.torres.lucas.taskmanager.model.Column
import mezzari.torres.lucas.taskmanager.repository.IBoxManager

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
class CreateColumnViewModel(
    private val dispatcher: IAppDispatcher,
    repository: IBoxManager
): ViewModel() {

    private val columnsBox: Box<Column> = repository.getBox(Column::class)

    var board: Board? = null

    val name: MutableLiveData<String> = MutableLiveData()

    val isFormValid: LiveData<Boolean> = Transformations.map(name) {
        !it.isNullOrEmpty() && it.isNotBlank()
    }

    private val _column: MutableLiveData<Column> = MutableLiveData()
    val column: LiveData<Column> get() = _column

    fun createColumn() {
        val board: Board = board ?: return
        val name: String = name.value ?: return

        if (isFormValid.value != true)
            return

        viewModelScope.launch(dispatcher.io) {
            addBoard(board.id, name)
        }
    }

    private fun addBoard(boardId: Long, name: String) {
        val createdColumn = Column(name = name, boardId = boardId)
        columnsBox.put(createdColumn)
        _column.postValue(createdColumn)
    }
}