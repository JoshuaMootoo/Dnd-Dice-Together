package com.dnd.dicelobby.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/** Room DAO for the [RollEntity] table. */
@Dao
interface RollDao {

    @Query("SELECT * FROM rolls ORDER BY timestamp DESC")
    fun getAllRolls(): Flow<List<RollEntity>>

    @Query("SELECT * FROM rolls ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentRolls(limit: Int = 100): List<RollEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoll(roll: RollEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRolls(rolls: List<RollEntity>)

    /** Update the hidden flag when a DM reveals a roll. */
    @Query("UPDATE rolls SET isHidden = 0 WHERE id = :rollId")
    suspend fun revealRoll(rollId: String)

    @Query("DELETE FROM rolls")
    suspend fun clearAll()

    @Query("DELETE FROM rolls WHERE id = :rollId")
    suspend fun deleteRoll(rollId: String)
}
