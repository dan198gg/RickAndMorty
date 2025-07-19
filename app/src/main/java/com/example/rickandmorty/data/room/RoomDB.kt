package com.example.rickandmorty.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.squareup.picasso.BuildConfig

@Database(entities = [MyRoomEntity::class], version= 1)
abstract class RoomDB: RoomDatabase() {
    abstract val dao: MyRoomDAO
    companion object{
        fun createDB(context: Context): RoomDB{
            return Room.databaseBuilder(context, RoomDB::class.java, "Characters.db").build()
        }
    }
}