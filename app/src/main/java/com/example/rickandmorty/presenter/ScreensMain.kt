package com.example.rickandmorty.presenter

sealed class ScreensMain(val route: String) {
    object AllCharacters: ScreensMain("all_characters")
    object FilterScreen: ScreensMain("filter_screen")
    object CharacterDetailsScreen: ScreensMain("character_details_screen")
}