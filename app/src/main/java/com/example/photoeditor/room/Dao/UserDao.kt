package com.example.photoeditor.room.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.photoeditor.room.Model.UserDetail

@Dao
interface UserDao {

    @Query("Select * from User")
    fun getUserDetails() : LiveData<List<UserDetail>>

    @Insert
    suspend fun insertUser(userDetail: UserDetail)
}
