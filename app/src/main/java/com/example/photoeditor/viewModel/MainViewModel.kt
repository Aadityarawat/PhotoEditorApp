package com.example.photoeditor.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditor.repository.UserRepository
import com.example.photoeditor.room.Model.UserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    fun getUser() : LiveData<List<UserDetail>>{
        return repository.getUser()
    }

    fun insertUser(userDetail: UserDetail){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(userDetail)
        }
    }
}