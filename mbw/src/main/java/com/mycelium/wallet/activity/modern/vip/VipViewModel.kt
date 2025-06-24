/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wallet.activity.modern.vip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.wallet.external.changelly2.remote.Api
import com.mycelium.wallet.update
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException

class VipViewModel : ViewModel() {

    private val userRepository = Api.statusRepository

    data class State(
        val isVip: Boolean = false,
        val error: ErrorType? = null,
        val progress: Boolean = true,
        val text: String = "",
    )

    private val _stateFlow = MutableStateFlow(State())
    val stateFlow = _stateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.statusFlow.collect { status ->
                val isVip = status.isVIP()
                _stateFlow.update { state -> state.copy(progress = false, isVip = isVip) }
            }
        }
    }

    fun updateVipText(text: String) {
        _stateFlow.update { s -> s.copy(text = text, error = null) }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        var errorType = ErrorType.UNEXPECTED
        if (e is HttpException) {
            if (e.code() == 404 || e.code() == 409 || e.code() == 401) {
                errorType = ErrorType.BAD_REQUEST
            }
        }
        _stateFlow.update { s -> s.copy(progress = false, error = errorType, isVip = false) }
    }

    fun applyCode() {
        viewModelScope.launch(exceptionHandler) {
            _stateFlow.update { s -> s.copy(progress = true, error = null, isVip = false) }
            val status = userRepository.applyVIPCode(_stateFlow.value.text)
            val error = if (status.isVIP()) null else ErrorType.BAD_REQUEST
            _stateFlow.update { s -> s.copy(progress = false, error = error) }
        }
    }

    fun resetState() {
        _stateFlow.update { s -> s.copy(progress = false, error = null, isVip = false) }
    }

    enum class ErrorType {
        UNEXPECTED,
        BAD_REQUEST,
    }
}