package com.example.rickandmorty.domain

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.rickandmorty.data.CharactersRepository
import com.example.rickandmorty.data.models.PersonData
import com.example.rickandmorty.data.retrofit.BaseUrlR
import com.example.rickandmorty.data.room.MyRoomEntity
import com.example.rickandmorty.data.room.RoomDB
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream

class CharacterRepositoryImpl(val context: Context): CharactersRepository {
    val roomDB = createRoomDB()
    private val service = BaseUrlR.serviceRetrofit

    override fun getCharacters(snapshotStateList: SnapshotStateList<MyRoomEntity>,
                               pages: MutableState<Int>,
                               flag: MutableState<Boolean>
    ,text:String){
        service.getCharacters() .enqueue(object:
            retrofit2.Callback<PersonData> {
            override fun onResponse(call: Call<PersonData>, response: Response<PersonData>) {
                CoroutineScope(Dispatchers.IO).launch {
                response.body()?.results!!.forEach {
                        val character = MyRoomEntity(
                            created = it.created,
                            episode = it.episode.toString(),
                            gender = it.gender,
                            id = it.id,
                            image = bitMapToByte(it.image),
                            location = it.location.name,
                            name = it.name,
                            origin = it.origin.name,
                            species = it.species,
                            status = it.status,
                            type = it.type,
                            url = it.url
                        )
                    roomDB.dao.getAllCharacters().forEach {    re ->
                        if (it.id == re.id){
                            roomDB.dao.delete(re)
                        }
                    }
                        roomDB.dao.insertCharacter(
                            character
                        )
                    }
                    snapshotStateList.clear()
                    roomDB.dao.getAllCharacters().forEach {
                            snapshotStateList.add(it)
                    }
                    pages.value = roomDB.dao.getAllCharacters().size / 8 + 1
                    flag.value = true
                }
            }

            override fun onFailure(call: Call<PersonData>, t: Throwable) {
                CoroutineScope(Dispatchers.IO).launch {
                    snapshotStateList.clear()
                    val allroomCh = getCharactersFromRoom()
                    allroomCh.forEach {
                            snapshotStateList.add(it)
                    }
                    flag.value  = true
                }
            }

        })

    }

    override fun createRoomDB(): RoomDB {
        return RoomDB.createDB(context = context)
    }

    override fun getCharactersFromRoom(): List<MyRoomEntity> {
       return roomDB.dao.getAllCharacters()
    }


    fun bitMapToByte(url:String): ByteArray{
        val bitmap: Bitmap = Picasso.get().load(url).get()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        return image
    }

}