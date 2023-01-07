package com.viol4tsf.noteappm.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteBody: String,
    @ColumnInfo(name = "creationDate", defaultValue = "0")
    val creationDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "groupName", defaultValue = "0")
    val groupName: String
): Parcelable