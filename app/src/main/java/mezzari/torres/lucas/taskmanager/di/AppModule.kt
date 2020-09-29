package mezzari.torres.lucas.taskmanager.di

import io.objectbox.Box
import mezzari.torres.lucas.conductor.source.generic.provider.ConductorProvider
import mezzari.torres.lucas.taskmanager.flow.MainConductor
import mezzari.torres.lucas.taskmanager.di.dispatcher.AppDispatcher
import mezzari.torres.lucas.taskmanager.di.dispatcher.IAppDispatcher
import mezzari.torres.lucas.taskmanager.flow.dialog.board.CreateBoardViewModel
import mezzari.torres.lucas.taskmanager.flow.dialog.column.CreateColumnViewModel
import mezzari.torres.lucas.taskmanager.flow.main.board.BoardViewModel
import mezzari.torres.lucas.taskmanager.flow.main.boards.BoardsViewModel
import mezzari.torres.lucas.taskmanager.flow.main.task.TaskViewModel
import mezzari.torres.lucas.taskmanager.model.Board
import mezzari.torres.lucas.taskmanager.repository.BoxManager
import mezzari.torres.lucas.taskmanager.repository.IBoxManager
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
val genericModule = module {
    single<IAppDispatcher> { AppDispatcher() }
}

val repositoryModule = module {
    single<IBoxManager> { BoxManager(androidApplication()) }
    single<Box<Board>> { get<IBoxManager>().getBox(Board::class) }
}

val viewModelModule = module {
    viewModel { BoardsViewModel(get(), get()) }
    viewModel { CreateBoardViewModel(get(), get()) }
    viewModel { CreateColumnViewModel(get(), get()) }
    viewModel { BoardViewModel(get(), get()) }
    viewModel { TaskViewModel(get(), get()) }
}

val conductorModule = module {
    single<MainConductor> { ConductorProvider[MainConductor::class] }
}

val appModule = listOf(genericModule, repositoryModule, viewModelModule, conductorModule)