package com.dnd.dicelobby.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "user_prefs")
private val PLAYER_ID_KEY      = stringPreferencesKey("player_id")
private val PLAYER_NAME_KEY    = stringPreferencesKey("player_name")
private val PLAYER_COLOR_KEY   = stringPreferencesKey("player_color")
private val PLAYER_RACE_KEY    = stringPreferencesKey("player_race")
private val PLAYER_SUBRACE_KEY = stringPreferencesKey("player_subrace")

/**
 * ViewModel for the Home screen.
 * Loads / persists the player's chosen display name and colour using DataStore.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _playerName  = MutableStateFlow("")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    private val _playerId    = MutableStateFlow(UUID.randomUUID().toString())
    val playerId: StateFlow<String> = _playerId.asStateFlow()

    private val _playerColor = MutableStateFlow("#E53935")
    val playerColor: StateFlow<String> = _playerColor.asStateFlow()

    private val _playerRace = MutableStateFlow("")
    val playerRace: StateFlow<String> = _playerRace.asStateFlow()

    private val _playerSubrace = MutableStateFlow("")
    val playerSubrace: StateFlow<String> = _playerSubrace.asStateFlow()

    init {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data
                .map { prefs ->
                    listOf(
                        prefs[PLAYER_ID_KEY]      ?: UUID.randomUUID().toString(),
                        prefs[PLAYER_NAME_KEY]    ?: "",
                        prefs[PLAYER_COLOR_KEY]   ?: "#E53935",
                        prefs[PLAYER_RACE_KEY]    ?: "",
                        prefs[PLAYER_SUBRACE_KEY] ?: ""
                    )
                }
                .collect { (id, name, color, race, subrace) ->
                    _playerId.value      = id
                    _playerName.value    = name
                    _playerColor.value   = color
                    _playerRace.value    = race
                    _playerSubrace.value = subrace
                }
        }
    }

    fun updateName(name: String) {
        _playerName.value = name
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_NAME_KEY] = name
            }
        }
    }

    fun updateColor(color: String) {
        _playerColor.value = color
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_COLOR_KEY] = color
            }
        }
    }

    fun updateRace(race: String) {
        _playerRace.value = race
        _playerSubrace.value = ""
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_RACE_KEY]    = race
                prefs[PLAYER_SUBRACE_KEY] = ""
            }
        }
    }

    fun updateSubrace(subrace: String) {
        _playerSubrace.value = subrace
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_SUBRACE_KEY] = subrace
            }
        }
    }

    /** Ensures a stable UUID is persisted across sessions. */
    fun ensurePlayerId() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                if (!prefs.contains(PLAYER_ID_KEY)) {
                    prefs[PLAYER_ID_KEY] = UUID.randomUUID().toString()
                }
            }
        }
    }
}
