package com.example.merchtools.ui.searchsku.edit_sku

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.ui.searchsku.SearchSkuEvent
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
                val updated = state.sku.copy(upc = event.userInput)
                state = state.copy(
                    sku = updated,
                    isUpcValid = validateUpc(updated)
                )
            }
            is EditSkuEvent.OnNameChanged -> {
                val updated = state.sku.copy(name = event.userInput)
                state = state.copy(
                    sku = updated,
                    isEntryValid = validateOtherFields(updated)
                )
            }
            is EditSkuEvent.OnCasePackChanged -> {
                val updated = state.sku.copy(casePack = event.userInput)
                state = state.copy(
                    sku = updated,
                    isEntryValid = validateOtherFields(updated)
                )
            }
            is EditSkuEvent.OnBrandChanged -> {
                val updated = state.sku.copy(brand = event.userInput)
                state = state.copy(
                    sku = updated,
                    isEntryValid = validateOtherFields(updated)
                )
            }
            is EditSkuEvent.SaveSku -> {
                saveSku()
            }
            is EditSkuEvent.OnImageUriChanged -> {
                state = state.copy(
                    sku = state.sku.copy(
                        imageUri = event.uri.toString()
                    )
                )
//                addProductImage(event.uri)
            }
            is EditSkuEvent.DiscardImageUri -> {
                state = state.copy(
                    sku = state.sku.copy(
                        imageUri = null
                    )
                )
            }
        }
    }

    private fun addProductImage(uri: Uri) {
            /*state = state.copy(isLoading = true)
            try {
                state = state.copy(
                    sku = state.sku.copy(
                        imageUri = uri.toString()
                    )
                )
                state = state.copy(isLoading = false)
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage("Image added"))
            } catch (e: Exception) {
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error"))
            } finally {
                state = state.copy(isLoading = false)
            }*/

    }

    private fun validateUpc(sku: Sku): Boolean {
        return (sku.upc.length in 12..<13 && sku.upc.isDigitsOnly())
    }

    private fun validateOtherFields(sku: Sku): Boolean {
        return (sku.name.isNotEmpty() && sku.brand.isNotEmpty())
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
        editSkuJob = skuRepository
            .getSkyByIdStream(skuId)
            .onStart {
                state = state.copy(isLoading = true, error = null)
            }
            .onEach { sku ->
                sku?.let {
                    state = state.copy(
                        sku = it,
                        isLoading = false,
                        isUpcValid = validateUpc(it),
                        isEntryValid = validateOtherFields(it)
                    )
                }
            }
            .catch { e ->
                state = state.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
            .launchIn(viewModelScope)
    }

}