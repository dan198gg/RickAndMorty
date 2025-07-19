package com.example.rickandmorty.presenter

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavController(vm: MainViewModel, context: Context) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreensMain.AllCharacters.route) {
        composable(ScreensMain.AllCharacters.route) {
            AllCharacters(vm, context, navController)
        }

        composable(ScreensMain.FilterScreen.route) {
            FilterScreen(vm.gender, vm.location, vm.name, vm.status,
                vm.type, vm.alltypes, vm.snapList, navController, vm.totalPages)

        }

        composable(ScreensMain.CharacterDetailsScreen.route) {
            CharacterDetailsScreen(vm.current!!, navController)
        }

    }
}