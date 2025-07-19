package com.example.rickandmorty.presenter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rickandmorty.data.room.MyRoomEntity



@Composable
fun CharacterDetailsScreen(currentCh: MyRoomEntity, navHostController: NavHostController) {
    val smallText = 25.sp
    val mediumText = 40.sp
    val heightScreen = LocalConfiguration.current.screenHeightDp
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
            Image(byteArrayToBitmap(currentCh.image).asImageBitmap(), "", modifier = Modifier.fillMaxWidth(0.9f).height((heightScreen/2).dp).align(
                Alignment.CenterHorizontally).padding(top = 10.dp))
            Spacer(Modifier.height(40.dp))
            Text(currentCh.name, fontSize = 54.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(
                Alignment.CenterHorizontally), textAlign = TextAlign.Center)
            Row(modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.Center) {

                Text(currentCh.species, fontSize = mediumText)
                Text("|", fontSize = mediumText)
                Text(currentCh.gender, fontSize = mediumText)
            }
            Text(
                if (currentCh.type == "") "type is not detected" else currentCh.type, fontSize = smallText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 5.dp))

                    Text("Status: ${currentCh.status}", fontSize = smallText, modifier = Modifier.align(
                        Alignment.CenterHorizontally))
            Text("Location: ${currentCh.location}", fontSize = smallText, modifier =  Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), textAlign = TextAlign.Center)
            Text("Created: ${currentCh.created}", fontSize = smallText, modifier = Modifier.align(
                Alignment.CenterHorizontally).padding(top = 5.dp), textAlign = TextAlign.Center)
         Button(onClick = {navHostController.navigate(ScreensMain.AllCharacters.route)}) {
             Row {
                 Icon(Icons.Default.ArrowBack , "")
                 Text("Назад")
             }
         }
        }
            }
    }

