package com.example.rickandmorty.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rickandmorty.data.models.Location
import com.example.rickandmorty.data.models.Origin


@Entity
data class MyRoomEntity (
    val created: String,
    val episode: String,
    val gender: String,
    @PrimaryKey(false)
    val id: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray,
    val location: String,
    val name: String,
    val origin: String,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)