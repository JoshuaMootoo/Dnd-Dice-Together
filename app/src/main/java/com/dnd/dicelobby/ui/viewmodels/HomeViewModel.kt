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
private val PLAYER_ID_KEY        = stringPreferencesKey("player_id")
private val PLAYER_NAME_KEY      = stringPreferencesKey("player_name")
private val PLAYER_COLOR_KEY     = stringPreferencesKey("player_color")
private val PLAYER_CHAR_NAME_KEY = stringPreferencesKey("player_char_name")
private val PLAYER_RACE_KEY      = stringPreferencesKey("player_race")
private val PLAYER_SUBRACE_KEY   = stringPreferencesKey("player_subrace")
private val PLAYER_CLASS_KEY     = stringPreferencesKey("player_class")
private val PLAYER_SUBCLASS_KEY  = stringPreferencesKey("player_subclass")
private val PLAYER_BACKGROUND_KEY= stringPreferencesKey("player_background")
private val PLAYER_ALIGNMENT_KEY = stringPreferencesKey("player_alignment")
private val PLAYER_LEVEL_KEY     = stringPreferencesKey("player_level")
private val PLAYER_XP_KEY        = stringPreferencesKey("player_xp")
private val PLAYER_STR_KEY       = stringPreferencesKey("player_str")
private val PLAYER_DEX_KEY       = stringPreferencesKey("player_dex")
private val PLAYER_CON_KEY       = stringPreferencesKey("player_con")
private val PLAYER_INT_KEY       = stringPreferencesKey("player_int")
private val PLAYER_WIS_KEY       = stringPreferencesKey("player_wis")
private val PLAYER_CHA_KEY       = stringPreferencesKey("player_cha")
private val PLAYER_ARMOR_KEY     = stringPreferencesKey("player_armor")
private val PLAYER_WEAPON_KEY    = stringPreferencesKey("player_weapon")
private val PLAYER_GEAR_KEY      = stringPreferencesKey("player_gear") // comma-separated

/**
 * ViewModel for the Home screen.
 * Loads / persists the player's character setup using DataStore.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _playerName  = MutableStateFlow("")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    private val _playerId    = MutableStateFlow(UUID.randomUUID().toString())
    val playerId: StateFlow<String> = _playerId.asStateFlow()

    private val _playerColor = MutableStateFlow("#E53935")
    val playerColor: StateFlow<String> = _playerColor.asStateFlow()

    private val _characterName = MutableStateFlow("")
    val characterName: StateFlow<String> = _characterName.asStateFlow()

    private val _playerRace = MutableStateFlow("")
    val playerRace: StateFlow<String> = _playerRace.asStateFlow()

    private val _playerSubrace = MutableStateFlow("")
    val playerSubrace: StateFlow<String> = _playerSubrace.asStateFlow()

    private val _playerClass = MutableStateFlow("")
    val playerClass: StateFlow<String> = _playerClass.asStateFlow()

    private val _playerSubclass = MutableStateFlow("")
    val playerSubclass: StateFlow<String> = _playerSubclass.asStateFlow()

    private val _background = MutableStateFlow("")
    val background: StateFlow<String> = _background.asStateFlow()

    private val _alignment = MutableStateFlow("")
    val alignment: StateFlow<String> = _alignment.asStateFlow()

    private val _level = MutableStateFlow(1)
    val level: StateFlow<Int> = _level.asStateFlow()

    private val _xp = MutableStateFlow(0)
    val xp: StateFlow<Int> = _xp.asStateFlow()

    private val _strScore = MutableStateFlow(10)
    val strScore: StateFlow<Int> = _strScore.asStateFlow()
    private val _dexScore = MutableStateFlow(10)
    val dexScore: StateFlow<Int> = _dexScore.asStateFlow()
    private val _conScore = MutableStateFlow(10)
    val conScore: StateFlow<Int> = _conScore.asStateFlow()
    private val _intScore = MutableStateFlow(10)
    val intScore: StateFlow<Int> = _intScore.asStateFlow()
    private val _wisScore = MutableStateFlow(10)
    val wisScore: StateFlow<Int> = _wisScore.asStateFlow()
    private val _chaScore = MutableStateFlow(10)
    val chaScore: StateFlow<Int> = _chaScore.asStateFlow()

    private val _startingArmor = MutableStateFlow("")
    val startingArmor: StateFlow<String> = _startingArmor.asStateFlow()

    private val _startingWeapon = MutableStateFlow("")
    val startingWeapon: StateFlow<String> = _startingWeapon.asStateFlow()

    private val _startingGear = MutableStateFlow<List<String>>(emptyList())
    val startingGear: StateFlow<List<String>> = _startingGear.asStateFlow()

    init {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collect { prefs ->
                _playerId.value       = prefs[PLAYER_ID_KEY]        ?: UUID.randomUUID().toString()
                _playerName.value     = prefs[PLAYER_NAME_KEY]      ?: ""
                _playerColor.value    = prefs[PLAYER_COLOR_KEY]     ?: "#E53935"
                _characterName.value  = prefs[PLAYER_CHAR_NAME_KEY] ?: ""
                _playerRace.value     = prefs[PLAYER_RACE_KEY]      ?: ""
                _playerSubrace.value  = prefs[PLAYER_SUBRACE_KEY]   ?: ""
                _playerClass.value    = prefs[PLAYER_CLASS_KEY]     ?: ""
                _playerSubclass.value = prefs[PLAYER_SUBCLASS_KEY]  ?: ""
                _background.value     = prefs[PLAYER_BACKGROUND_KEY]?: ""
                _alignment.value      = prefs[PLAYER_ALIGNMENT_KEY] ?: ""
                _level.value          = prefs[PLAYER_LEVEL_KEY]?.toIntOrNull() ?: 1
                _xp.value             = prefs[PLAYER_XP_KEY]?.toIntOrNull()    ?: 0
                _strScore.value       = prefs[PLAYER_STR_KEY]?.toIntOrNull()   ?: 10
                _dexScore.value       = prefs[PLAYER_DEX_KEY]?.toIntOrNull()   ?: 10
                _conScore.value       = prefs[PLAYER_CON_KEY]?.toIntOrNull()   ?: 10
                _intScore.value       = prefs[PLAYER_INT_KEY]?.toIntOrNull()   ?: 10
                _wisScore.value       = prefs[PLAYER_WIS_KEY]?.toIntOrNull()   ?: 10
                _chaScore.value       = prefs[PLAYER_CHA_KEY]?.toIntOrNull()   ?: 10
                _startingArmor.value  = prefs[PLAYER_ARMOR_KEY]     ?: ""
                _startingWeapon.value = prefs[PLAYER_WEAPON_KEY]    ?: ""
                val gear              = prefs[PLAYER_GEAR_KEY]      ?: ""
                _startingGear.value   = if (gear.isBlank()) emptyList() else gear.split(",")
            }
        }
    }

    fun updateName(name: String) {
        _playerName.value = name
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs -> prefs[PLAYER_NAME_KEY] = name }
        }
    }

    fun updateCharacterName(name: String) {
        _characterName.value = name
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs -> prefs[PLAYER_CHAR_NAME_KEY] = name }
        }
    }

    fun updateBackground(bg: String) {
        _background.value = bg
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs -> prefs[PLAYER_BACKGROUND_KEY] = bg }
        }
    }

    fun updateAlignment(al: String) {
        _alignment.value = al
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs -> prefs[PLAYER_ALIGNMENT_KEY] = al }
        }
    }

    fun updateLevel(lvl: Int) {
        _level.value = lvl
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs -> prefs[PLAYER_LEVEL_KEY] = lvl.toString() }
        }
    }

    fun updateXp(xp: Int) {
        _xp.value = xp
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs -> prefs[PLAYER_XP_KEY] = xp.toString() }
        }
    }

    fun updateAbilityScore(stat: String, value: Int) {
        val clamped = value.coerceIn(1, 30)
        when (stat) {
            "STR" -> _strScore.value = clamped
            "DEX" -> _dexScore.value = clamped
            "CON" -> _conScore.value = clamped
            "INT" -> _intScore.value = clamped
            "WIS" -> _wisScore.value = clamped
            "CHA" -> _chaScore.value = clamped
        }
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                val key = when (stat) {
                    "STR" -> PLAYER_STR_KEY; "DEX" -> PLAYER_DEX_KEY; "CON" -> PLAYER_CON_KEY
                    "INT" -> PLAYER_INT_KEY; "WIS" -> PLAYER_WIS_KEY
                    else  -> PLAYER_CHA_KEY
                }
                prefs[key] = clamped.toString()
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
        _playerRace.value    = race
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

    fun updateClass(cls: String) {
        _playerClass.value    = cls
        _playerSubclass.value = ""
        _startingArmor.value  = ""
        _startingWeapon.value = ""
        _startingGear.value   = emptyList()
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_CLASS_KEY]    = cls
                prefs[PLAYER_SUBCLASS_KEY] = ""
                prefs[PLAYER_ARMOR_KEY]    = ""
                prefs[PLAYER_WEAPON_KEY]   = ""
                prefs[PLAYER_GEAR_KEY]     = ""
            }
        }
    }

    fun updateSubclass(subclass: String) {
        _playerSubclass.value = subclass
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_SUBCLASS_KEY] = subclass
            }
        }
    }

    fun updateArmor(armor: String) {
        _startingArmor.value = armor
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_ARMOR_KEY] = armor
            }
        }
    }

    fun updateWeapon(weapon: String) {
        _startingWeapon.value = weapon
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_WEAPON_KEY] = weapon
            }
        }
    }

    fun toggleGearItem(item: String) {
        val current = _startingGear.value.toMutableList()
        if (current.contains(item)) current.remove(item) else current.add(item)
        _startingGear.value = current
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PLAYER_GEAR_KEY] = current.joinToString(",")
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
