package mezzari.torres.lucas.taskmanager.di.dispatcher

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class AppDispatcher(
    override var main: CoroutineContext = Dispatchers.Main,
    override var io: CoroutineContext = Dispatchers.IO
) : IAppDispatcher