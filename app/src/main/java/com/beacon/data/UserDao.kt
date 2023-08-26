package com.beacon.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//다오(DB와 상호작용(CRUD)
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}
