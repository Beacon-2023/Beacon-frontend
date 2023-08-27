package com.beacon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DisasterDAO {
    @Insert
    suspend fun insert(dataEntity: DisasterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disasters: List<DisasterEntity>)

    @Update
    suspend fun update(dataEntity: DisasterEntity)

    @Query("SELECT * FROM data")
    suspend fun getAllDisaster(): List<DisasterEntity>

    @Query("SELECT * FROM data")
    suspend fun getAllDisasters(): List<DisasterEntity>

    @Query("SELECT ischecked FROM data")
    suspend fun getIsCheckedValues(): List<Int>

    @Query("UPDATE data SET ischecked = :isCheckedValue WHERE id = :position")
    suspend fun updateIsCheckedValue(position: Int, isCheckedValue: Int)

}
