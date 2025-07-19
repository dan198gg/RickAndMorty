package com.example.rickandmorty.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MyRoomDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(characterEntity: MyRoomEntity)

    @Delete
    suspend fun delete(characterEntity: MyRoomEntity)

    @Query("SELECT * FROM MyRoomEntity")
    fun getAllCharacters(): List<MyRoomEntity>
}
