package com.beacon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class DisasterEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val disasterName: String,
    val ischecked: Int
)