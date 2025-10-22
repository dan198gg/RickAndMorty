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
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
data class CharacterFilters(
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val type: String = "",
    val gender: String = ""
)

class CharacterRepositoryImpl(val context: Context): CharactersRepository {
    val roomDB = createRoomDB()
    private val service = BaseUrlR.serviceRetrofit

//    override fun getCharacters(snapshotStateList: SnapshotStateList<MyRoomEntity>,
//                               pages: MutableState<Int>,
//                               flag: MutableState<Boolean>
//    ,text:String){
//        service.getCharacters() .enqueue(object:
//            retrofit2.Callback<PersonData> {
//            override fun onResponse(call: Call<PersonData>, response: Response<PersonData>) {
//                CoroutineScope(Dispatchers.IO).launch {
//                response.body()?.results!!.forEach {
//                        val character = MyRoomEntity(
//                            created = it.created,
//                            episode = it.episode.toString(),
//                            gender = it.gender,
//                            id = it.id,
//                            image = bitMapToByte(it.image),
//                            location = it.location.name,
//                            name = it.name,
//                            origin = it.origin.name,
//                            species = it.species,
//                            status = it.status,
//                            type = it.type,
//                            url = it.url
//                        )
//                    roomDB.dao.getAllCharacters().forEach {    re ->
//                        if (it.id == re.id){
//                            roomDB.dao.delete(re)
//                        }
//                    }
//                        roomDB.dao.insertCharacter(
//                            character
//                        )
//                    }
//                    snapshotStateList.clear()
//                    roomDB.dao.getAllCharacters().forEach {
//                            snapshotStateList.add(it)
//                    }
//                    pages.value = roomDB.dao.getAllCharacters().size / 8 + 1
//                    flag.value = true
//                }
//            }
//
//            override fun onFailure(call: Call<PersonData>, t: Throwable) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    snapshotStateList.clear()
//                    val allroomCh = getCharactersFromRoom()
//                    allroomCh.forEach {
//                            snapshotStateList.add(it)
//                    }
//                    flag.value  = true
//                }
//            }
//
//        })
//
//    }


    override fun getCharacters(
        snapshotStateList: SnapshotStateList<MyRoomEntity>,
        pages: MutableState<Int>,
        flag: MutableState<Boolean>,
        text: String,
        progressState: MutableState<Float>? ,
        onAllCharactersLoaded: ((List<MyRoomEntity>) -> Unit)?
    ) {
        progressState?.value = 0f

        service.getCharacters().enqueue(object : retrofit2.Callback<PersonData> {
            override fun onResponse(call: Call<PersonData>, response: Response<PersonData>) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val characters = response.body()?.results ?: emptyList()
                        val totalCharacters = characters.size
                        var processedCharacters = 0

                        progressState?.value = 10f

                        characters.forEach { characterData ->
                            val character = MyRoomEntity(
                                created = characterData.created,
                                episode = characterData.episode.toString(),
                                gender = characterData.gender,
                                id = characterData.id,
                                image = bitMapToByte(characterData.image),
                                location = characterData.location.name,
                                name = characterData.name,
                                origin = characterData.origin.name,
                                species = characterData.species,
                                status = characterData.status,
                                type = characterData.type,
                                url = characterData.url
                            )
                            roomDB.dao.insertCharacter(character)
                            processedCharacters++

                            val currentProgress = 10f + (processedCharacters.toFloat() / totalCharacters * 80f)
                            progressState?.value = currentProgress
                        }

                        val allCharacters = roomDB.dao.getAllCharacters()

                        withContext(Dispatchers.Main) {
                            onAllCharactersLoaded?.invoke(allCharacters)
                            snapshotStateList.clear()
                            snapshotStateList.addAll(allCharacters)
                            pages.value = if (allCharacters.isEmpty()) 1 else (allCharacters.size + 7) / 8
                            flag.value = true
                            progressState?.value = 100f
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progressState?.value = 100f
                            flag.value = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PersonData>, t: Throwable) {
                CoroutineScope(Dispatchers.IO).launch {
                    val allCharacters = roomDB.dao.getAllCharacters()

                    withContext(Dispatchers.Main) {
                        onAllCharactersLoaded?.invoke(allCharacters)

                        snapshotStateList.clear()
                        snapshotStateList.addAll(allCharacters)
                        pages.value = if (allCharacters.isEmpty()) 1 else (allCharacters.size + 7) / 8
                        flag.value = true
                        progressState?.value = 100f
                    }
                }
            }
        })
    }


    override fun getCharactersWithFilters(
        filters: CharacterFilters,
        snapshotStateList: SnapshotStateList<MyRoomEntity>,
        pages: MutableState<Int>,
        flag: MutableState<Boolean>,
        progressState: MutableState<Float>? ,
        onAllCharactersLoaded: ((List<MyRoomEntity>) -> Unit)?
    ) {
        progressState?.value = 0f

        val call = service.getCharactersWithFilters(
            name = filters.name.takeIf { it.isNotEmpty() },
            status = filters.status.takeIf { it.isNotEmpty() },
            species = filters.species.takeIf { it.isNotEmpty() },
            type = filters.type.takeIf { it.isNotEmpty() },
            gender = filters.gender.takeIf { it.isNotEmpty() }
        )

        call.enqueue(object : retrofit2.Callback<PersonData> {
            override fun onResponse(call: Call<PersonData>, response: Response<PersonData>) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val characters = response.body()?.results ?: emptyList()
                        val totalCharacters = characters.size
                        var processedCharacters = 0

                        progressState?.value = 10f


                        characters.forEach { characterData ->
                            val character = MyRoomEntity(
                                created = characterData.created,
                                episode = characterData.episode.toString(),
                                gender = characterData.gender,
                                id = characterData.id,
                                image = bitMapToByte(characterData.image),
                                location = characterData.location.name,
                                name = characterData.name,
                                origin = characterData.origin.name,
                                species = characterData.species,
                                status = characterData.status,
                                type = characterData.type,
                                url = characterData.url
                            )
                            roomDB.dao.insertCharacter(character)
                            processedCharacters++

                            val currentProgress = 10f + (processedCharacters.toFloat() / totalCharacters * 80f)
                            progressState?.value = currentProgress
                        }

                        val allCharacters = roomDB.dao.getAllCharacters()

                        withContext(Dispatchers.Main) {
                            onAllCharactersLoaded?.invoke(allCharacters)

                            snapshotStateList.clear()
                            snapshotStateList.addAll(allCharacters)
                            pages.value = if (allCharacters.isEmpty()) 1 else (allCharacters.size + 7) / 8
                            flag.value = true
                            progressState?.value = 100f
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progressState?.value = 100f
                            flag.value = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PersonData>, t: Throwable) {
                CoroutineScope(Dispatchers.IO).launch {
                    val allCharacters = roomDB.dao.getAllCharacters()

                    withContext(Dispatchers.Main) {
                        onAllCharactersLoaded?.invoke(allCharacters)

                        snapshotStateList.clear()
                        snapshotStateList.addAll(allCharacters)
                        pages.value = if (allCharacters.isEmpty()) 1 else (allCharacters.size + 7) / 8
                        flag.value = true
                        progressState?.value = 100f
                    }
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