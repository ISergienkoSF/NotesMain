package com.viol4tsf.noteappm.db.relations

import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Relation
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note

data class GroupWithNotes(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "groupName",
        entityColumn = "groupName"
    )
    val notes: List<Note>
)
