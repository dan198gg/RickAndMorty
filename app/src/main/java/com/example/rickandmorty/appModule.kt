package com.example.rickandmorty


import com.example.rickandmorty.data.CharactersRepository
import com.example.rickandmorty.domain.CharacterRepositoryImpl
import com.example.rickandmorty.presenter.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CharactersRepository>{
        CharacterRepositoryImpl(get())
    }
    viewModel{MainViewModel(get())}
}