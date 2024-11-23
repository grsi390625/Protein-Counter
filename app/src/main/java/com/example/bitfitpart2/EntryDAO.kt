package com.example.bitfitpart2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDAO {
    @Query("SELECT * FROM entry_table ORDER BY id DESC")
    fun getAll(): Flow<List<EntryEntity>>

    @Insert
    fun insert(entry: EntryEntity)

    @Query("DELETE FROM entry_table WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM entry_table")
    fun deleteAll()
}

