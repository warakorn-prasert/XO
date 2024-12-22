package com.korn.portfolio.xo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.korn.portfolio.xo.XOApplication
import com.korn.portfolio.xo.repo.Game
import com.korn.portfolio.xo.repo.XORepository
import kotlinx.coroutines.launch

class SharedViewModel(private val repo: XORepository) : ViewModel() {
    val games = repo.games

    // game and bot
    var gameToPlay: Pair<Game, String?>? by mutableStateOf(null)

    fun deleteGame(vararg game: Game) {
        viewModelScope.launch {
            repo.deleteGame(*game)
        }
    }

    fun deleteAllGames() {
        viewModelScope.launch {
            repo.deleteAllGames()
        }
    }

    fun saveGame(vararg game: Game) {
        viewModelScope.launch {
            repo.insertGame(*game)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as XOApplication
                SharedViewModel(app.repo)
            }
        }
    }
}