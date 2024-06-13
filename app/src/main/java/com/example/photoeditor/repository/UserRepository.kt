package com.example.photoeditor.repository

import androidx.lifecycle.LiveData
import com.example.photoeditor.room.Dao.UserDatabase
import com.example.photoeditor.room.Model.UserDetail
import javax.inject.Inject


class UserRepository @Inject constructor(private val userDatabase: UserDatabase) {

    fun getUser() : LiveData<List<UserDetail>>{
        return userDatabase.userDao().getUserDetails()
    }

    suspend fun insertUser(userDetail: UserDetail){
        userDatabase.userDao().insertUser(userDetail)
    }
}