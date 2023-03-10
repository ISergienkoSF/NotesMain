package com.viol4tsf.noteappm.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "groups")
@Parcelize
data class Group(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", defaultValue = "0")
    val id: Int,
    val groupName: String
): Parcelable
