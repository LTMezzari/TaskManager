package mezzari.torres.lucas.taskmanager.flow

import androidx.navigation.fragment.findNavController
import mezzari.torres.lucas.conductor.annotation.ConductorAnnotation
import mezzari.torres.lucas.conductor.source.generic.annotated.AnnotatedConductor
import mezzari.torres.lucas.conductor.source.generic.annotated.AnnotatedFlowCycle
import mezzari.torres.lucas.taskmanager.R
import mezzari.torres.lucas.taskmanager.flow.main.board.BoardFragment
import mezzari.torres.lucas.taskmanager.flow.main.boards.BoardsFragment
import mezzari.torres.lucas.taskmanager.flow.main.task.TaskFragment
import mezzari.torres.lucas.taskmanager.model.Board
import mezzari.torres.lucas.taskmanager.model.Task

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class MainConductor: AnnotatedConductor() {
    var board: Board? = null
    var task: Task? = null

    override fun start() {
        super.start()
        board = null
        task = null
    }

    // ----------------------- BoardsFragment
    @ConductorAnnotation(BoardsFragment::class, AnnotatedFlowCycle.STEP_INITIATED)
    private fun onBoardsFragmentInitiated(fragment: BoardsFragment) {
        board = null
        task = null
    }

    @ConductorAnnotation(BoardsFragment::class, AnnotatedFlowCycle.NEXT)
    private fun onBoardsFragmentNext(fragment: BoardsFragment) {
        val navController = fragment.findNavController()
        navController.navigate(R.id.action_boardsFragment_to_boardFragment)
    }

    // ----------------------- BoardsFragment

    @ConductorAnnotation(BoardFragment::class, AnnotatedFlowCycle.STEP_INITIATED)
    private fun onBoardFragmentInitiated(fragment: BoardFragment) {
        fragment.viewModel?.board = board
    }

    @ConductorAnnotation(BoardFragment::class, AnnotatedFlowCycle.NEXT)
    private fun onBoardFragmentNext(fragment: BoardFragment) {
        val navController = fragment.findNavController()
        navController.navigate(R.id.action_boardFragment_to_taskFragment)
    }

    // ----------------------- TaskFragment

    @ConductorAnnotation(TaskFragment::class, AnnotatedFlowCycle.STEP_INITIATED)
    private fun onTaskFragmentInitiated(fragment: TaskFragment) {
        fragment.viewModel?.board = board
        fragment.viewModel?.task = task
    }

    @ConductorAnnotation(TaskFragment::class, AnnotatedFlowCycle.NEXT)
    private fun onTaskFragmentNext(fragment: TaskFragment) {
        task = null
        val navController = fragment.findNavController()
        navController.popBackStack()
    }
}