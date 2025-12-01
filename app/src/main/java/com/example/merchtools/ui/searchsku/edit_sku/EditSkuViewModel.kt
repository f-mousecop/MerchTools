package com.example.merchtools.ui.searchsku.edit_sku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.ui.searchsku.SearchSkuUiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSkuViewModel @Inject constructor(
    private val skuRepository: SkuRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val skuId: Long = checkNotNull(savedStateHandle["skuId"])
    var state by mutableStateOf(EditSkuState())

    private val _uiEffect = MutableSharedFlow<SearchSkuUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var editSkuJob: Job? = null

    init {
        getSkuStream(skuId)
    }

    fun onEvent(event: EditSkuEvent) {
        when (event) {
            is EditSkuEvent.OnUpcChanged -> {
                state = state.copy(
                    sku = state.sku.copy(
                        upc = event.userInput),
                    isEntryValid = true)
            }
            is EditSkuEvent.OnNameChanged -> {
                state = state.copy(
                    sku = state.sku.copy(
                        name = event.userInput),
                    isEntryValid = true)
            }
            is EditSkuEvent.OnCasePackChanged -> {
                state = state.copy(
                    sku = state.sku.copy(
                        casePack = event.userInput),
                    isEntryValid = true)
            }
            is EditSkuEvent.OnBrandChanged -> {
                state = state.copy(
                    sku = state.sku.copy(
                        brand = event.userInput),
                    isEntryValid = true)
            }
            is EditSkuEvent.SaveSku -> {
                saveSku()
            }
        }
    }

    private fun saveSku() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                delay(2000L)

                skuRepository.update(state.sku)

                _uiEffect.emit(SearchSkuUiEffect.ShowMessage("Audit item saved"))
                delay(2000L)
                _uiEffect.emit(SearchSkuUiEffect.NavigateUp)

            } catch (e: Exception) {
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error"))
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun getSkuStream(skuId: Long) {
        editSkuJob?.cancel()
        editSkuJob = viewModelScope.launch {
            try {
                skuRepository
                    .getSkyByIdStream(skuId)
                println("DEBUG: stream emitted sku = $skuId")
            } catch (e: Exception) {
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
            /*.onStart {
                state = state.copy(isLoading = true, error = null)
            }
            .onEach { sku ->
                println("DEBUG: stream emitted sku = ${sku?.skuId}")
                sku?.let {
                    state = state.copy(
                        sku = it,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                state = state.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)*/
    }

}