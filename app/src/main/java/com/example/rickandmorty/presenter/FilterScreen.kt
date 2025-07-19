package com.example.rickandmorty.presenter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.rickandmorty.data.room.MyRoomEntity
import com.example.rickandmorty.domain.FIlterCharactersWihtAttributes

@Composable
fun FilterScreen(gender: MutableState<String>,
                 location: MutableState<String>,
                 name: MutableState<String>,
                 status: MutableState<String>,
                 type: MutableState<String>,
                 alltypes: MutableList<String>,
                 snapshotStateList: SnapshotStateList<MyRoomEntity>,
                 navHostController: NavHostController,
                 pages: MutableState<Int>
                 ){

    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var expanded3 by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Text("Filters", textAlign = TextAlign.Center, fontSize = 45.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = name.value,
            onValueChange = {
                name.value = it.replace("\n", "")
            },
            shape = RoundedCornerShape(size = 30.dp),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .shadow(40.dp, shape = RoundedCornerShape(30.dp)),
            placeholder = { Text(text = "Имя") }
        )

        TextField(
            value = location.value,
            onValueChange = {
                location.value = it.replace("\n", "")
            },
            shape = RoundedCornerShape(size = 30.dp),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .shadow(40.dp, shape = RoundedCornerShape(30.dp))
            ,
            placeholder = { Text(text = "Location") }
        )
        Button(onClick = {expanded = true}, Modifier.fillMaxWidth(0.9f)) {
            Text(gender.value)
            DropdownMenu(
                expanded,
                onDismissRequest = {
                    expanded = false
                }, modifier = Modifier.padding(top = 10.dp)
            ) {

                DropdownMenuItem(onClick = {
                    gender.value = "Male"
                }, text = { Text("Male") })
                DropdownMenuItem(onClick = {
                    gender.value = "Female"
                }, text = { Text("Female") })
            }
        }

        Button(onClick = {expanded2 = true}, Modifier.fillMaxWidth(0.9f)) {
            Text(type.value)
            DropdownMenu(
                expanded2,
                onDismissRequest = {
                    expanded2 = false
                }, modifier = Modifier.padding(top = 10.dp)
            ) {
                alltypes.forEach {
                    DropdownMenuItem(
                        onClick = {
                            type.value = it
                        },
                        text = { Text(it) }
                    )
                }
            }
        }
        Button(onClick = {expanded3 = true}, Modifier.fillMaxWidth(0.9f)) {
            Text(status.value)

            DropdownMenu(
                expanded3,
                onDismissRequest = {
                    expanded3 = false
                }
            ) {
                DropdownMenuItem(onClick = {
                    status.value = "Dead"
                }, text = { Text("Dead") })
                DropdownMenuItem(onClick = {
                    status.value = "Alive"
                }, text = { Text("Alive") })
            }
        }
            Button(
                onClick = {
                    FIlterCharactersWihtAttributes().filter1(
                        gender.value,
                        location.value,
                        name.value,
                        status.value,
                        type.value,
                        snapshotStateList = snapshotStateList
                    )
                    navHostController.navigate(ScreensMain.AllCharacters.route)
                    pages.value = snapshotStateList.size / 8 + 1
                }
            ) {
                Text("Filter")
            }


    }
}
