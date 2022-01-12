package dev.fredag.cheerwithme.happening

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.data.backend.Happening
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HappeningsViewModel @Inject constructor(private val happeningsRepository: HappeningsRepository) : ViewModel() {

    private val _happenings = MutableLiveData<List<Happening>>()
    val happenings: LiveData<List<Happening>> get() = _happenings

    fun loadHappenings() {
        viewModelScope.launch {
            happeningsRepository.getHappenings()
                .onStart {  }
                .catch {  }
                .collect {
                _happenings.value = it
            }
        }
    }
}