package com.example.rickandmorty.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@SuppressLint("ConfigurationScreenWidthHeight", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCharacters(vm: MainViewModel, context: Context, navHostController: NavHostController) {
    val roundingDelta = 30.dp
    val heightLayout = LocalConfiguration.current.screenHeightDp
    var isRefreshing by remember { mutableStateOf(false) }

    val progress by remember { vm.progress }
    val snapList by remember { mutableStateOf(vm.snapList) }
    val hasData = snapList.isNotEmpty()
    val isLoading = progress < 100f && !hasData


    LaunchedEffect(1) {
        if (vm.snapList.isEmpty() && vm.progress.value == 0f) {
            vm.getCh(
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .height((heightLayout / 10).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = vm.textFilter.value,
                onValueChange = {
                    vm.textFilter.value = it.replace("\n", "")
                },
                shape = RoundedCornerShape(size = roundingDelta),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .shadow(10.dp, shape = RoundedCornerShape(roundingDelta))
                    .weight(6f),
                placeholder = { Text(text = "Найти персонажа...") }
            )
            Button(
                onClick = {
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            vm.filterLocalList(vm.textFilter.value)

                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Попробуйте еще раз!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .padding(end = 10.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Поиск")
            }
        }

        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = "Загрузка персонажей: ${progress.toInt()}%",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }


        Box(modifier = Modifier.weight(1f)) {

            if (!isLoading && !hasData) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Нет персонажей",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Button(
                        onClick = {
                            vm.progress.value = 0f
                            vm.getCh(
                            )
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Попробовать снова")
                    }
                }
            }

            if (hasData) {
                println("DEBUG: Showing character list with ${snapList.size} items")
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        vm.progress.value = 0f
                        vm.snapList.clear()
                        vm.textFilter.value = ""
                        vm.resetFilters()
                            try {
                                if (vm.flag.value) {
                                    vm.getCh(
                                    )
                                }
                            }catch (e:ConcurrentModificationException){
                                Toast.makeText(context, "Подождите", Toast.LENGTH_SHORT).show()
                            }
                            isRefreshing = false
                    },
                ) {
                    val pagerState = rememberPagerState(
                        pageCount = { maxOf(1, vm.totalPages.value) },
                        initialPage = 0
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (vm.totalPages.value > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(vm.totalPages.value) { index ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (pagerState.currentPage == index) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                }
                                            )
                                    )
                                }
                            }
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->
                            val items = getPageItems(vm.snapList, page, vm.itemsPerPage)
                            println("DEBUG: Page $page showing ${items.size} items")

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(items) { character ->
                                    CharacterCard(
                                        character = character,
                                        heightLayout = heightLayout,
                                        onClick = {
                                            vm.current = character
                                            navHostController.navigate(ScreensMain.CharacterDetailsScreen.route)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (hasData) {
            Button(
                onClick = {
                    val uniqueTypes = vm.snapList.map { it.type }.distinct()
                    navHostController.navigate(ScreensMain.FilterScreen.route)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Фильтры",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: MyRoomEntity,
    heightLayout: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(12.dp)
            .height((heightLayout / 3).dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Image(
                    bitmap = byteArrayToBitmap(character.image).asImageBitmap(),
                    contentDescription = character.name,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(topStart = 16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(end = 4.dp)
                        ) {
                            drawCircle(
                                color = when (character.status) {
                                    "Alive" -> Color.Green
                                    "Dead" -> Color.Red
                                    else -> Color.Gray
                                }
                            )
                        }
                        Text(
                            text = character.status,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = character.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                Text(
                    text = "${character.species} | ${character.gender}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun getPageItems(
    snapList: List<MyRoomEntity>,
    page: Int,
    itemsPerPage: Int
): List<MyRoomEntity> {
    return try {
        val startIndex = page * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, snapList.size)
        snapList.subList(startIndex, endIndex)
    } catch (e: Exception) {
        emptyList()
    }
}