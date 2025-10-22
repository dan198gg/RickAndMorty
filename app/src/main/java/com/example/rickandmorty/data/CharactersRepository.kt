package com.example.rickandmorty.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.rickandmorty.data.models.PersonData
import com.example.rickandmorty.data.room.MyRoomEntity
import com.example.rickandmorty.data.room.RoomDB
import com.example.rickandmorty.domain.CharacterFilters

interface CharactersRepository {
    fun createRoomDB(): RoomDB
    fun getCharacters(
        snapshotStateList: SnapshotStateList<MyRoomEntity>,
        pages: MutableState<Int>,
        flag: MutableState<Boolean>,
        text: String,
        progressState: MutableState<Float>? = null,
        onAllCharactersLoaded: ((List<MyRoomEntity>) -> Unit)? = null
    )

    fun getCharactersWithFilters(
        filters: CharacterFilters,
        snapshotStateList: SnapshotStateList<MyRoomEntity>,
        pages: MutableState<Int>,
        flag: MutableState<Boolean>,
        progressState: MutableState<Float>? = null,
        onAllCharactersLoaded: ((List<MyRoomEntity>) -> Unit)? = null
    )
        fun getCharactersFromRoom(): List<MyRoomEntity>

}
