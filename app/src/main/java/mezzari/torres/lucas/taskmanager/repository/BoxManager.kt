package mezzari.torres.lucas.taskmanager.repository

import android.app.Application
import io.objectbox.Box
import io.objectbox.BoxStore
import mezzari.torres.lucas.taskmanager.model.MyObjectBox
import kotlin.reflect.KClass

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
class BoxManager(application: Application): IBoxManager {
    private var boxStore: BoxStore = MyObjectBox
        .builder()
        .androidContext(application)
        .build()

    override fun <T>getBox(entity: KClass<*>): Box<T> {
        return boxStore.boxFor(entity.java) as Box<T>
    }

    inline fun <reified T> getBox(): Box<T> {
        return getBox(T::class)
    }
}