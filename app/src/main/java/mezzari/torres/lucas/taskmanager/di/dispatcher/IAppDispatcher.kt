package mezzari.torres.lucas.taskmanager.di.dispatcher

import kotlin.coroutines.CoroutineContext

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
interface IAppDispatcher {
    var main: CoroutineContext
    var io: CoroutineContext
}