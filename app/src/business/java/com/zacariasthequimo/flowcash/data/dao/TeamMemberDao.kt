package com.zacariasthequimo.flowcash.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zacariasthequimo.flowcash.data.entity.TeamMember
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamMemberDao {
    @Query("SELECT * FROM team_members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<TeamMember>>

    @Query("SELECT * FROM team_members WHERE id = :id")
    suspend fun getMemberById(id: Long): TeamMember?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: TeamMember): Long

    @Update
    suspend fun update(member: TeamMember)

    @Delete
    suspend fun delete(member: TeamMember)
}
