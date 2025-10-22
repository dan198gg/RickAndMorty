package com.example.rickandmorty.presenter

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.rickandmorty.data.CharactersRepository
import com.example.rickandmorty.data.room.MyRoomEntity
import com.example.rickandmorty.domain.CharacterFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(val charactersRepository: CharactersRepository) : ViewModel() {

    val snapList = SnapshotStateList<MyRoomEntity>()
    private val allCharactersCache = mutableListOf<MyRoomEntity>() // Кэш всех персонажей

    val itemsPerPage: Int = 8
    val totalPages = mutableIntStateOf(1)
    val flag = mutableStateOf(false)
    val textFilter = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val progress = mutableStateOf(0f)

    // Фильтры
    val currentFilters = mutableStateOf(CharacterFilters())
    val isFilterActive = mutableStateOf(false)
    val useNetworkFiltering = mutableStateOf(false) // Выбор типа фильтрации

    // Опции для фильтров
    val statusOptions = listOf("", "Alive", "Dead", "Unknown")
    val genderOptions = listOf("", "Female", "Male", "Genderless", "Unknown")
    val speciesOptions = mutableListOf<String>()
    val typeOptions = mutableListOf<String>()

    var current: MyRoomEntity? = null

    // Основная загрузка данных
    fun getCh() {
        flag.value = false
        charactersRepository.getCharacters(
            snapshotStateList = snapList,
            pages = totalPages,
            flag = flag,
            text = "",
            progressState = progress,
            onAllCharactersLoaded = { characters ->
                // Сохраняем полный список в кэш
                allCharactersCache.clear()
                allCharactersCache.addAll(characters)
                updateFilterOptions()
            }
        )
    }

    // Функция фильтрации (выбирает между локальной и сетевой)
    fun applyFilters(filters: CharacterFilters, useNetwork: Boolean = false) {
        currentFilters.value = filters
        isFilterActive.value = hasActiveFilters(filters)
        useNetworkFiltering.value = useNetwork

        if (useNetwork) {
            // Сетевая фильтрация
            applyNetworkFilters(filters)
        } else {
            // Локальная фильтрация
            applyLocalFilters()
        }
    }

    // Сетевая фильтрация
    private fun applyNetworkFilters(filters: CharacterFilters) {
        progress.value = 0f
        isLoading.value = true

        charactersRepository.getCharactersWithFilters(
            filters = filters,
            snapshotStateList = snapList,
            pages = totalPages,
            flag = flag,
            progressState = progress,
            onAllCharactersLoaded = { characters ->
                // Обновляем кэш
                allCharactersCache.clear()
                allCharactersCache.addAll(characters)
                updateFilterOptions()
            }
        )
    }

    // Локальная фильтрация
    private fun applyLocalFilters() {
        CoroutineScope(Dispatchers.Main).launch {
            val filteredList = withContext(Dispatchers.Default) {
                filterCharactersLocally(
                    query = textFilter.value,
                    filters = if (isFilterActive.value) currentFilters.value else null
                )
            }

            // Обновляем snapList
            snapList.clear()
            snapList.addAll(filteredList)
            updatePagination(filteredList.size)
        }
    }

    // Локальная фильтрация персонажей
    private fun filterCharactersLocally(
        query: String,
        filters: CharacterFilters?
    ): List<MyRoomEntity> {
        val sourceList = if (allCharactersCache.isEmpty()) snapList else allCharactersCache

        return sourceList.filter { character ->
            // Фильтр по текстовому запросу (поиск по имени)
            val matchesQuery = query.isEmpty() ||
                    character.name.contains(query, ignoreCase = true)

            // Фильтры по характеристикам
            val matchesFilters = filters?.let { filter ->
                (filter.name.isEmpty() || character.name.contains(filter.name, ignoreCase = true)) &&
                        (filter.status.isEmpty() || character.status.equals(filter.status, ignoreCase = true)) &&
                        (filter.species.isEmpty() || character.species.equals(filter.species, ignoreCase = true)) &&
                        (filter.type.isEmpty() || character.type.equals(filter.type, ignoreCase = true)) &&
                        (filter.gender.isEmpty() || character.gender.equals(filter.gender, ignoreCase = true))
            } ?: true

            matchesQuery && matchesFilters
        }
    }

    // Быстрая локальная фильтрация по тексту
    fun filterLocalList(query: String) {
        textFilter.value = query
        if (useNetworkFiltering.value && isFilterActive.value) {
            // Если используем сетевые фильтры, применяем всё заново
            applyFilters(currentFilters.value, useNetwork = true)
        } else {
            // Локальная фильтрация
            applyLocalFilters()
        }
    }

    // Сброс всех фильтров
    fun resetFilters() {
        currentFilters.value = CharacterFilters()
        isFilterActive.value = false
        textFilter.value = ""
        useNetworkFiltering.value = false

        // Показываем все данные
        if (allCharactersCache.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                snapList.clear()
                snapList.addAll(allCharactersCache)
                updatePagination(allCharactersCache.size)
            }
        } else {
            // Если кэша нет, перезагружаем данные
            getCh()
        }
    }

    // Загрузка всех данных (сброс к исходному состоянию)
    fun loadAllCharacters() {
        resetFilters()
        getCh()
    }

    // Проверка есть ли активные фильтры
    private fun hasActiveFilters(filters: CharacterFilters): Boolean {
        return filters.name.isNotEmpty() ||
                filters.status.isNotEmpty() ||
                filters.species.isNotEmpty() ||
                filters.type.isNotEmpty() ||
                filters.gender.isNotEmpty()
    }

    // Обновление пагинации
    private fun updatePagination(listSize: Int) {
        totalPages.value = if (listSize == 0) 1 else (listSize + itemsPerPage - 1) / itemsPerPage
    }

    // Обновление опций для фильтров
    fun updateFilterOptions() {
        CoroutineScope(Dispatchers.Default).launch {
            val sourceList = if (allCharactersCache.isNotEmpty()) allCharactersCache else snapList

            val allSpecies = sourceList.map { it.species }
                .distinct()
                .sorted()

            val allTypes = sourceList.map { it.type }
                .filter { it.isNotEmpty() }
                .distinct()
                .sorted()

            withContext(Dispatchers.Main) {
                speciesOptions.clear()
                speciesOptions.addAll(allSpecies)

                typeOptions.clear()
                typeOptions.addAll(allTypes)
            }
        }
    }

    init {
        getCh()
    }
}