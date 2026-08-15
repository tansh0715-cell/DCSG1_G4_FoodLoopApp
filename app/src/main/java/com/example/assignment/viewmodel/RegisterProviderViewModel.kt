package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.repository.AuthRepository
import com.example.assignment.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterProviderViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _restaurantName = MutableStateFlow("")
    val restaurantName: StateFlow<String> = _restaurantName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _licenseUrl = MutableStateFlow("") // 模拟执照URL，实际需上传文件获得
    val licenseUrl: StateFlow<String> = _licenseUrl.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun updateRestaurantName(input: String) { _restaurantName.value = input }
    fun updateEmail(input: String) { _email.value = input }
    fun updatePhone(input: String) { _phone.value = input }
    fun updateAddress(input: String) { _address.value = input }
    fun updateLicenseUrl(url: String) { _licenseUrl.value = url }
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
            val result = authRepo.registerProvider(
                email = _email.value,
                password = _password.value,
                restaurantName = _restaurantName.value,
                phone = _phone.value,
                address = _address.value,
                licenseUrl = _licenseUrl.value
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