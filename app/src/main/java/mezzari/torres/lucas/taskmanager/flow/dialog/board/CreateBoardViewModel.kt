package mezzari.torres.lucas.taskmanager.flow.dialog.board

import androidx.lifecycle.*
import io.objectbox.Box
import kotlinx.coroutines.launch
import mezzari.torres.lucas.taskmanager.di.dispatcher.IAppDispatcher
import mezzari.torres.lucas.taskmanager.model.Board
import mezzari.torres.lucas.taskmanager.repository.IBoxManager

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class CreateBoardViewModel(
    private val dispatcher: IAppDispatcher,
    repository: IBoxManager
): ViewModel() {

    private val boardsBox: Box<Board> = repository.getBox(Board::class)

    val name: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()

    val isFormValid: LiveData<Boolean> = Transformations.map(name) {
        !it.isNullOrEmpty() && it.isNotBlank()
    }

    private val _board: MutableLiveData<Board> = MutableLiveData()
    val board: MutableLiveData<Board> get() =  _board

    fun createBoard() {
        val name: String = name.value ?: return
        val description: String? = description.value

        if (isFormValid.value != true)
            return

        viewModelScope.launch(dispatcher.io) {
            addBoard(name, description)
        }
    }

    private fun addBoard(name: String, description: String?) {
        val createdBoard = Board(name = name, description = description)
        boardsBox.put(createdBoard)
        _board.postValue(createdBoard)
    }
}