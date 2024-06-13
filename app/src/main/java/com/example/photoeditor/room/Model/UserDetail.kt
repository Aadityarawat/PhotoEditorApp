package com.example.photoeditor.room.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserDetail(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val firstName : String,
    val lastName : String,
    val email : String,
    val password : String
)
