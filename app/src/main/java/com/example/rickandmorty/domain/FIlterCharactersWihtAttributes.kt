package com.example.rickandmorty.domain

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.rickandmorty.data.room.MyRoomEntity

class FIlterCharactersWihtAttributes {
    fun filter1(gender: String,
                location: String,
                name: String,
                status: String,
                type: String,
                snapshotStateList: SnapshotStateList<MyRoomEntity>){
        val s2 = SnapshotStateList<MyRoomEntity>()
        snapshotStateList.forEach{
            if ( gender in it.gender && location in it.location && name in it.name
                && status in it.status && type in it.type){
                s2.add(it)
            }
        }
        snapshotStateList.clear()
        s2.forEach {
            snapshotStateList.add(it)
        }
    }
}