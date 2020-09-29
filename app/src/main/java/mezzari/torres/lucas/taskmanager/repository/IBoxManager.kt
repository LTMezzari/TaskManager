package mezzari.torres.lucas.taskmanager.repository

import io.objectbox.Box
import kotlin.reflect.KClass

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
interface IBoxManager {
    fun <T>getBox(entity: KClass<*>): Box<T>
}