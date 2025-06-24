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
package com.mycelium.wallet.activity.changelog

import android.app.Application
import androidx.lifecycle.*
import com.mycelium.wallet.activity.changelog.datasource.ChangeLogDataSource
import kotlinx.coroutines.launch

internal class ChangeLogViewModel(application: Application) : AndroidViewModel(application) {
    private val _changeLogDataSource by lazy { ChangeLogDataSource(application.applicationContext) }
    private val _fullReleasesList = liveData {
        val changeLog = _changeLogDataSource.readChangeLog()
        emit(changeLog)
    }
    val displayingListType = MutableLiveData(DisplayingListType.LATEST_RELEASE)
    val releases = displayingListType.switchMap { type ->
        _fullReleasesList.map { list ->
            when (type) {
                DisplayingListType.FULL -> list
                else -> takeFirstRelease(list)
            }
        }
    }

    init {
        viewModelScope.launch {
            _changeLogDataSource.saveCurrentVersion()
        }
    }

    fun toggleDisplayingListType() {
        displayingListType.value = when (displayingListType.value) {
            DisplayingListType.LATEST_RELEASE -> DisplayingListType.FULL
            else -> DisplayingListType.LATEST_RELEASE
        }
    }

    private fun takeFirstRelease(source: List<ChangeLogUiModel>): List<ChangeLogUiModel> {
        val resultList = mutableListOf<ChangeLogUiModel>()
        run loop@{
            source.forEachIndexed { index, model ->
                if (index != 0 && model is ChangeLogUiModel.Release)
                    return@loop
                resultList.add(model)
            }
        }
        return resultList
    }

    enum class DisplayingListType {
        FULL, LATEST_RELEASE
    }
}
