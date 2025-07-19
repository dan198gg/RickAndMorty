package com.example.rickandmorty.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rickandmorty.data.room.MyRoomEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCharacters(vm:MainViewModel, context: Context,
                  navHostController: NavHostController) {
    val emptyStr = remember { mutableStateOf("") }
    val roundingDelta = 30.dp
    val heightLayout = LocalConfiguration.current.screenHeightDp
    var isRefreshing by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()){
        if (vm.snapList.isEmpty()) emptyStr.value = "Empty"
        else emptyStr.value = ""
        Text(emptyStr.value, fontSize = 40.sp, modifier = Modifier.align(Alignment.Center))

    Column {
        Row (Modifier.height((heightLayout/10).dp), verticalAlignment = Alignment.CenterVertically){
            TextField(
                value = vm.textFilter.value,
                onValueChange = {
                    vm.textFilter.value = it.replace("\n", "")
                },
                shape = RoundedCornerShape(size = roundingDelta),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .shadow(40.dp, shape = RoundedCornerShape(roundingDelta))
                    .weight(6f),
                placeholder = { Text(text = "Найти") }
            )
            Button(onClick = {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.filterSnapList()
                        vm.gender.value = ""
                        vm.location.value = ""
                        vm.name.value = ""
                        vm.type.value = ""
                        vm.status.value = ""
                    }
                }catch (e:Exception){
                    Toast.makeText(context, "Попробуйте еще раз!", Toast.LENGTH_SHORT).show()
                }
            }, Modifier
                .weight(1f)
                .clip(CircleShape)) {
                Image(Icons.Default.Search, "")
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                CoroutineScope(Dispatchers.IO).launch {
                    vm.charactersRepository.getCharactersFromRoom()
                }
                isRefreshing = false
            },
        ) {
            var pagerState = rememberPagerState {
                vm.totalPages.value
            }
            HorizontalPager(pagerState) { pageit ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2)
                ) {
                    var itms: List<MyRoomEntity>
                    if (vm.flag.value == true && vm.snapList.size != 1) {
                        try {
                            itms = vm.snapList
                                .slice(pageit * vm.itemsPerPage until pageit * vm.itemsPerPage + vm.itemsPerPage)
                        } catch (e: Exception) {
                            try {
                                itms = vm.snapList
                                    .slice(pageit * vm.itemsPerPage until vm.snapList.lastIndex)
                            } catch (e: Exception) {
                                itms = vm.snapList
                            }
                        }
                    } else {
                        itms = vm.snapList

                    }
                    items(itms) {

                        Card( onClick = {
                            vm.current = it
                            navHostController.navigate(ScreensMain.CharacterDetailsScreen.route)
                        }, modifier = Modifier
                                .padding(15.dp)
                                .height((heightLayout / 3).dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Gray)
                        ) {
                            Column {
                                Box(Modifier.weight(1f)) {
                                    Image(
                                        byteArrayToBitmap(it.image).asImageBitmap(),
                                        "",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        Modifier
                                            .align(Alignment.BottomEnd)
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 20.dp
                                                )
                                            )
                                            .background(
                                                Color.DarkGray
                                            )
                                    ) {
                                        Row(
                                            Modifier
                                                .fillMaxWidth(0.5f)
                                                .fillMaxHeight(0.1f)
                                        ) {
                                            if (it.status == "Alive") {
                                                Canvas(
                                                    Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .padding(1.dp)
                                                ) {
                                                    drawCircle(color = Color.Green)
                                                }
                                            } else {
                                                Canvas(
                                                    Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .padding(1.dp)
                                                ) {
                                                    drawCircle(color = Color.Red)
                                                }
                                            }
                                            AutoResizeText(
                                                text = it.status,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .weight(1f),
                                                fontSizeRange = FontSizeRange(7.sp, 20.sp)
                                            )
                                        }
                                    }
                                }
                                Column(
                                    Modifier
                                        .weight(0.6f)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(modifier = Modifier) {
                                        Column {
                                            Text(
                                                it.name,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                                fontSize = 25.sp
                                            )
                                            Text(
                                                "${it.species}|${it.gender}",
                                                color = Color.LightGray,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        }
        Button(onClick = {
            vm.snapList.forEach{
                vm.alltypes.add(it.type)
            }
            vm.alltypes = vm.alltypes.distinct().toMutableList()
            navHostController.navigate(ScreensMain.FilterScreen.route)
        }, modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 10.dp, end = 10.dp)) {
            Icon(Icons.Default.Settings, "")
        }
    }
}