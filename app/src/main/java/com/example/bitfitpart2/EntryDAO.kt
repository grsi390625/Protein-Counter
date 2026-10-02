package com.example.bitfitpart2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDAO {
    @Query("SELECT * FROM entry_table ORDER BY id DESC")
    fun getAll(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entry_table WHERE LOWER(food_name) LIKE '%' || LOWER(:query) || '%' ESCAPE '\\' ORDER BY id DESC")
    fun search(query: String): Flow<List<EntryEntity>>

    @Insert
    fun insert(entry: EntryEntity)

    @Update
    fun update(entry: EntryEntity)

    @Query("DELETE FROM entry_table WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM entry_table")
    fun deleteAll()
}

