package com.example.datn.viewmodel.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.Resutl_RefreshToken
import com.example.datn.data.dataresult.User
import com.example.datn.data.model.AcceptOTP
import com.example.datn.data.model.Auth
import com.example.datn.data.model.ForgetPass
import com.example.datn.data.model.Register
import com.example.datn.data.model.google_input
import com.example.datn.data.model.loginWithGoogle
import com.example.datn.data.model.sendOTP
import com.example.datn.repository.repositoryAuth
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(private val repositoryAuth: repositoryAuth) : ViewModel() {

    private val _loginResult: MutableLiveData<ResponseResult<User>> = MutableLiveData()
    val loginResult: LiveData<ResponseResult<User>> get() = _loginResult

    private val _googleRequest: MutableLiveData<ResponseResult<Resutl_RefreshToken>> = MutableLiveData()
    val googleResult: LiveData<ResponseResult<Resutl_RefreshToken>> get() = _googleRequest

    private val _loginWithGG: MutableLiveData<ResponseResult<User>> = MutableLiveData()
    val loginWithGoogle: LiveData<ResponseResult<User>> get() = _loginWithGG
    private var _registerResult: MutableLiveData<ResponseResult<ResultMessage>?> = MutableLiveData()
    var registerResult: LiveData<ResponseResult<ResultMessage>?> = _registerResult

    private val _acceptResult: MutableLiveData<ResponseResult<ResultMessage>?> = MutableLiveData()
    var acceptResult: LiveData<ResponseResult<ResultMessage>?> = _acceptResult

    private val _resultForgetPass: MutableLiveData<ResponseResult<ResultMessage>?> = MutableLiveData()
    var resultForget: LiveData<ResponseResult<ResultMessage>?>? = _resultForgetPass

    private val _resultOTP: MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultOTP: LiveData<ResponseResult<ResultMessage>> = _resultOTP

    private val _resultCheckAccount: MutableLiveData<ResponseResult<ResultMessage>?> = MutableLiveData()
    var resultCheckAccount: LiveData<ResponseResult<ResultMessage>?> = _resultCheckAccount

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resultLogout : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultLogout : LiveData<ResponseResult<ResultMessage>> get() = _resultLogout

    fun authLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryAuth.authLogout()
                if (response.isSuccessful) {
                    val logout = response.body()!!
                    _resultLogout.postValue(ResponseResult.Success(logout))
                } else {
                    val errorMessage = "Logout unsuccessful: ${response.message()}"
                    _resultLogout.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultLogout.postValue(ResponseResult.Error("Network connection error2!"))
            } catch (e: HttpException) {
                _resultLogout.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultLogout.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }

    fun authCheckAccount(sendOTP: sendOTP) {
        _resultCheckAccount.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryAuth.authCheckAccount(sendOTP)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultCheckAccount.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorMessage = "Account does not exist : ${response.message()}"
                    _resultCheckAccount.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultCheckAccount.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCheckAccount.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCheckAccount.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _resultOTP.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultOTP.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultOTP.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultOTP.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultOTP.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _resultForgetPass.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultForgetPass.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultForgetPass.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultForgetPass.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultForgetPass.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _acceptResult.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorMessage = "Registration failed: ${response.message()}"
                    _acceptResult.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _acceptResult.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _acceptResult.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _acceptResult.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _registerResult.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorMessage = "Register unsuccessful: ${response.message()}"
                    _registerResult.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _registerResult.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _registerResult.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _registerResult.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _loginWithGG.postValue(ResponseResult.Success(user))
                } else {
                    val errorMessage = "Login unsuccessful: ${response.message()}"
                    _loginWithGG.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _loginWithGG.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _loginWithGG.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _loginWithGG.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _googleRequest.postValue(ResponseResult.Success(gg))
                } else {
                    val errorMessage = "Login unsuccessful: ${response.message()}"
                    _googleRequest.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _googleRequest.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _googleRequest.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _googleRequest.postValue(ResponseResult.Error("An unknown error has occurred!"))
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
                    _loginResult.postValue(ResponseResult.Success(user))
                } else {
                    val errorMessage = "Login unsuccessful: ${response.message()}"
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else errorMessage
                    _loginResult.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _loginResult.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _loginResult.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _loginResult.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }

        }
    }


}