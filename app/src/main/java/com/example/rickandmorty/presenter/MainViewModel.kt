package com.example.rickandmorty.presenter

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.rickandmorty.data.CharactersRepository
import com.example.rickandmorty.data.room.MyRoomEntity
import kotlinx.coroutines.CoroutineScope


class MainViewModel(val charactersRepository: CharactersRepository):ViewModel() {

    var snapList = SnapshotStateList<MyRoomEntity>()
    val itemsPerPage: Int = 8
    var totalPages = mutableIntStateOf(snapList.size + itemsPerPage - 1 / itemsPerPage)
    val flag = mutableStateOf(false)
    val textFilter = mutableStateOf("")

    var gender = mutableStateOf("")
    var location = mutableStateOf("")
    var name = mutableStateOf("")
    var status = mutableStateOf("")
    var type = mutableStateOf("")
    var alltypes = mutableListOf<String>()

    var current:MyRoomEntity? = null

    fun getCh(){
        flag.value = false
        charactersRepository.getCharacters(snapList, totalPages, flag,textFilter.value)
    }


    fun filterSnapList() {

        if (textFilter.value.isNotEmpty()) {
            var sn2 = SnapshotStateList<MyRoomEntity>()
            charactersRepository.getCharactersFromRoom().forEach {
                    if (textFilter.value.lowercase() in it.name.lowercase()
                        || textFilter.value.lowercase() == it.name.lowercase()) {
                        sn2.add(it)
                        }
                    }
            snapList.clear()
            sn2.forEach {
                snapList.add(it)
            }
            totalPages.value = snapList.size / 8 + 1
        }
        else{
                snapList.clear()
                charactersRepository.getCharactersFromRoom().forEach {
                    snapList.add(it)
                }
            totalPages.value = snapList.size / 8 + 1
            }

    }
    init{
        charactersRepository.getCharacters(snapList, totalPages,flag,textFilter.value)
    }
}