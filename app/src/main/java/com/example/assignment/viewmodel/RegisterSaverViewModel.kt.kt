package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterSaverViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun updateFullName(input: String) { _fullName.value = input }
    fun updateEmail(input: String) { _email.value = input }
    fun updatePhone(input: String) { _phone.value = input }
    fun updatePassword(input: String) { _password.value = input }
    fun updateConfirmPassword(input: String) { _confirmPassword.value = input }

    fun register() {
        if (_password.value != _confirmPassword.value) {
            _uiState.value = UiState.Error("[X] Passwords don't match.")
            return
        }
        if (_password.value.length < 6) {
            _uiState.value = UiState.Error("[>_<] Password must be at least 6 characters long.")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = authRepo.registerSaver(
                email = _email.value,
                password = _password.value,
                fullName = _fullName.value,
                phone = _phone.value
            )
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: ":( Registration failed, please try again.") }
            )
        }
    }

    fun resetState() {
        _uiState.value = UiState.Loading
    }
}