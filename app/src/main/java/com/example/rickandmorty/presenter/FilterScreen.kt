package com.example.rickandmorty.presenter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(vm: MainViewModel, navHostController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Фильтры персонажей",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            TextButton(onClick = {
                vm.resetFilters()
                navHostController.popBackStack()
            }) {
                Text("Сбросить")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = vm.currentFilters.value.name,
                onValueChange = { newValue ->
                    vm.currentFilters.value = vm.currentFilters.value.copy(name = newValue)
                },
                label = { Text("Имя персонажа") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: Rick") }
            )


            Column {
                Text(
                    "Статус",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vm.statusOptions) { status ->
                        val isSelected = vm.currentFilters.value.status == status
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                vm.currentFilters.value = vm.currentFilters.value.copy(
                                    status = if (isSelected) "" else status
                                )
                            },
                            label = {
                                Text(
                                    if (status.isEmpty()) "Любой" else status,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }


            OutlinedTextField(
                value = vm.currentFilters.value.species,
                onValueChange = { newValue ->
                    vm.currentFilters.value = vm.currentFilters.value.copy(species = newValue)
                },
                label = { Text("Вид") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: Human, Alien") }
            )


            OutlinedTextField(
                value = vm.currentFilters.value.type,
                onValueChange = { newValue ->
                    vm.currentFilters.value = vm.currentFilters.value.copy(type = newValue)
                },
                label = { Text("Тип") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: Genetic experiment") }
            )


            Column {
                Text(
                    "Гендер",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vm.genderOptions) { gender ->
                        val isSelected = vm.currentFilters.value.gender == gender
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                vm.currentFilters.value = vm.currentFilters.value.copy(
                                    gender = if (isSelected) "" else gender
                                )
                            },
                            label = {
                                Text(
                                    if (gender.isEmpty()) "Любой" else gender,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                vm.applyFilters(vm.currentFilters.value)
                navHostController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Применить фильтры",
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (vm.isFilterActive.value) {
            Spacer(modifier = Modifier.height(16.dp))
            ActiveFiltersBadges(vm) {
                vm.resetFilters()
            }
        }
    }
}

@Composable
fun ActiveFiltersBadges(vm: MainViewModel, onReset: () -> Unit) {
    val filters = vm.currentFilters.value
    val activeFilters = listOfNotNull(
        filters.name.takeIf { it.isNotEmpty() }?.let { "Имя: $it" },
        filters.status.takeIf { it.isNotEmpty() }?.let { "Статус: $it" },
        filters.species.takeIf { it.isNotEmpty() }?.let { "Вид: $it" },
        filters.type.takeIf { it.isNotEmpty() }?.let { "Тип: $it" },
        filters.gender.takeIf { it.isNotEmpty() }?.let { "Гендер: $it" }
    )

    if (activeFilters.isNotEmpty()) {
        Column {
            Text(
                "Активные фильтры:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeFilters) { filter ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = onReset
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                filter,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Удалить",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}