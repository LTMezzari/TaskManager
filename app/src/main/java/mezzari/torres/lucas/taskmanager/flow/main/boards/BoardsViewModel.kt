package mezzari.torres.lucas.taskmanager.flow.main.boards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.objectbox.Box
import kotlinx.coroutines.launch
import mezzari.torres.lucas.taskmanager.di.dispatcher.IAppDispatcher
import mezzari.torres.lucas.taskmanager.model.Board
import mezzari.torres.lucas.taskmanager.repository.IBoxManager

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class BoardsViewModel(
    private val dispatcher: IAppDispatcher,
    repository: IBoxManager
): ViewModel() {

    private val boardsBox: Box<Board> = repository.getBox(Board::class)

    private val _boards: MutableLiveData<List<Board>> = MutableLiveData()
    val boards: LiveData<List<Board>> get() = _boards

    init {
        loadBoards()
    }

    fun addBoard(name: String?, description: String?) {
        if (name.isNullOrEmpty())
            return

        boardsBox.put(Board(name = name, description = description))
    }

    fun loadBoards() {
        viewModelScope.launch(dispatcher.io) {
            loadBoardsAsync()
        }
    }

    private fun loadBoardsAsync() {
        _boards.postValue(boardsBox.all)
    }
}