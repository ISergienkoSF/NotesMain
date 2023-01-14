package com.viol4tsf.noteappm.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.viol4tsf.noteappm.db.relations.GroupWithNotes
import com.viol4tsf.noteappm.model.Group

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroup(group: Group)

    @Update
    suspend fun updateGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("SELECT * FROM groups ORDER BY groupName DESC")
    fun getAllGroups(): LiveData<List<Group>>

    @Transaction
    @Query("SELECT * FROM groups WHERE groupName = :groupName")
    fun getGroupWithNotes(groupName: String): LiveData<List<GroupWithNotes>>

    @Query("SELECT groupName FROM groups")
    fun getGroup():List<String>
}