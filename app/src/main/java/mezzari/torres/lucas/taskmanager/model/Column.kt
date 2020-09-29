package mezzari.torres.lucas.taskmanager.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Transient
import io.objectbox.annotation.Id

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
@Entity
class Column (
    @Id
    var id: Long = 0,
    var boardId: Long,
    var name: String,
    var position: Int = 0,
    @Transient
    var board: Board? = null,
    @Transient
    var tasks: List<Task> = arrayListOf()
) {
    override fun toString(): String {
        return name
    }
}