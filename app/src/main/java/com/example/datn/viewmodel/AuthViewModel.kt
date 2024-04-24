package com.example.datn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.ResultMessage
import com.example.datn.data.Resutl_RefreshToken
import com.example.datn.data.User
import com.example.datn.data.model.AcceptOTP
import com.example.datn.data.model.Auth
import com.example.datn.data.model.ForgetPass
import com.example.datn.data.model.Register
import com.example.datn.data.model.google_input
import com.example.datn.data.model.loginWithGoogle
import com.example.datn.data.model.sendOTP
import com.example.datn.repository.repositoryAuth
import com.example.datn.utils.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(private val repositoryAuth: repositoryAuth) : ViewModel() {

    private val _loginResult: MutableLiveData<DataResult<User>> = MutableLiveData()
    val loginResult: LiveData<DataResult<User>> get() = _loginResult

    private val _googleRequest: MutableLiveData<DataResult<Resutl_RefreshToken>> = MutableLiveData()
    val googleResult: LiveData<DataResult<Resutl_RefreshToken>> get() = _googleRequest

    private val _loginWithGG: MutableLiveData<DataResult<User>> = MutableLiveData()
    val loginWithGoogle: LiveData<DataResult<User>> get() = _loginWithGG
    private var _registerResult: MutableLiveData<DataResult<ResultMessage>?> = MutableLiveData()
    var registerResult: LiveData<DataResult<ResultMessage>?> = _registerResult

    private val _acceptResult: MutableLiveData<DataResult<ResultMessage>?> = MutableLiveData()
    var acceptResult: LiveData<DataResult<ResultMessage>?> = _acceptResult

    private val _resultForgetPass: MutableLiveData<DataResult<ResultMessage>?> = MutableLiveData()
    var resultForget: LiveData<DataResult<ResultMessage>?>? = _resultForgetPass

    private val _resultOTP: MutableLiveData<DataResult<ResultMessage>> = MutableLiveData()
    val resultOTP: LiveData<DataResult<ResultMessage>> = _resultOTP

    private val _resultCheckAccount: MutableLiveData<DataResult<ResultMessage>?> = MutableLiveData()
    var resultCheckAccount: LiveData<DataResult<ResultMessage>?> = _resultCheckAccount

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun authCheckAccount(sendOTP: sendOTP) {
        _resultCheckAccount.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryAuth.authCheckAccount(sendOTP)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultCheckAccount.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Account does not exist : ${response.message()}"
                    _resultCheckAccount.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultCheckAccount.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCheckAccount.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCheckAccount.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun authSendOTP(sendOTP: sendOTP) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Log.e("email",email)
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryAuth.authSendOtp(sendOTP)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultOTP.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Registration failed OTP: ${response.message()}"
                    _resultOTP.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultOTP.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultOTP.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultOTP.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false) // Hide ProgressBar
            }
        }
    }

    fun authForgetPassword(forgetPass: ForgetPass) {
        _resultForgetPass.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryAuth.authForgetPassword(forgetPass)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultForgetPass.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Password change failed: ${response.message()}"
                    _resultForgetPass.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultForgetPass.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultForgetPass.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultForgetPass.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


    fun authAcceptRegister(acceptOTP: AcceptOTP) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryAuth.authAcceptRegister(acceptOTP)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _acceptResult.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Registration failed: ${response.message()}"
                    _acceptResult.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _acceptResult.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _acceptResult.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _acceptResult.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false) // Hide ProgressBar
            }
        }
    }

    fun authRegister(register: Register) {
        _registerResult.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryAuth.register1(register)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _registerResult.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Register unsuccessful: ${response.message()}"
                    _registerResult.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _registerResult.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _registerResult.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _registerResult.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false) // Hide ProgressBar
            }
        }
    }

    fun loginWithGoogle(lg: loginWithGoogle) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAuth.loginWithGoogle(lg)
                if (response.isSuccessful) {
                    val user = response.body()!!
                    repositoryAuth.saveAccessToken(user.user.access_token)
                    _loginWithGG.postValue(DataResult.Success(user))
                } else {
                    val errorMessage = "Login unsuccessful: ${response.message()}"
                    _loginWithGG.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _loginWithGG.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _loginWithGG.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _loginWithGG.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }

        }
    }

    fun googleRequest(google: google_input) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAuth.getRefreshToken(google)
                if (response.isSuccessful) {
                    val gg = response.body()!!
                    _googleRequest.postValue(DataResult.Success(gg))
                } else {
                    val errorMessage = "Login unsuccessful: ${response.message()}"
                    _googleRequest.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _googleRequest.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _googleRequest.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _googleRequest.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }

        }
    }

    fun login(auth: Auth) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAuth.login(auth)
                if (response.isSuccessful) {
                    val user = response.body()!!
                    repositoryAuth.saveAccessToken(user.user.access_token)
                    _loginResult.postValue(DataResult.Success(user))
                } else {
                    val errorMessage = "Login unsuccessful: ${response.message()}"
                    _loginResult.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _loginResult.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _loginResult.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _loginResult.postValue(DataResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }

        }
    }


}