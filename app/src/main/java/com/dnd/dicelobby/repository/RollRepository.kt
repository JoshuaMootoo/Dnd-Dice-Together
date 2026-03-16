package com.dnd.dicelobby.repository

import com.dnd.dicelobby.database.RollDao
import com.dnd.dicelobby.database.RollEntity
import com.dnd.dicelobby.models.Roll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Abstracts access to the local [RollDao] and exposes a clean domain-model API
 * to the ViewModel layer.
 */
class RollRepository(private val rollDao: RollDao) {

    /** Reactive stream of all rolls, newest first. */
    val allRolls: Flow<List<Roll>> = rollDao.getAllRolls().map { list ->
        list.map { it.toRoll() }
    }

    suspend fun insert(roll: Roll) = rollDao.insertRoll(RollEntity.fromRoll(roll))

    suspend fun insertAll(rolls: List<Roll>) =
        rollDao.insertRolls(rolls.map { RollEntity.fromRoll(it) })

    /** Called when a DM reveals a hidden roll; clears the hidden flag in the DB. */
    suspend fun revealRoll(rollId: String) = rollDao.revealRoll(rollId)

    suspend fun clearHistory() = rollDao.clearAll()
}
