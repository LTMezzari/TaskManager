package mezzari.torres.lucas.taskmanager.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
@Entity
class Task (
    @Id
    var id: Long,
    var columnId: Long,
    var boardId: Long,
    var title: String,
    var description: String?,
    var parentId: Long? = null,
    var position: Int = 0,
    var isHindered: Boolean = false,
    @Transient
    var column: Column? = null,
    @Transient
    var parent: Task? = null
) {
    override fun toString(): String {
        return title
    }
}