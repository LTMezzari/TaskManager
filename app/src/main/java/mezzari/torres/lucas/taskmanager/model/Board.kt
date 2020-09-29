package mezzari.torres.lucas.taskmanager.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * @author Lucas T. Mezzari
 * @since 15/09/2020
 */
@Entity
class Board (
    @Id var id: Long = 0,
    var name: String,
    var description: String?
)