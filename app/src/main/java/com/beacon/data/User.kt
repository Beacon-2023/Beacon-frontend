package com.beacon.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//테이블을 만들자
@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "email") var email: String?,
    @ColumnInfo(name = "id") var id: String?,
    @ColumnInfo(name = "pw") var pw: String?
)