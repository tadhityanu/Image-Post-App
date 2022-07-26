package com.example.submissionintermediate.Login


import androidx.lifecycle.*
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference

import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    fun saveUser(user: User){
        viewModelScope.launch {
            pref.saveUser(User(user.name, user.email, user.password, user.userId, user.token, user.isLogin) )
        }
    }

    fun logIn(){
        viewModelScope.launch {
            pref.login()
        }
    }

}