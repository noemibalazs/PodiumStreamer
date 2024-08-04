package viewmodel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.mirego.konnectivity.Konnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import model.FavoriteStream
import repository.PayloadRepository

class FavoriteViewModel(
    konnectivity: Konnectivity,
    private val repository: PayloadRepository,
) : BaseViewModel(konnectivity) {

    private var _payloadsState = MutableStateFlow(emptyList<FavoriteStream>())
    val payloadState: StateFlow<List<FavoriteStream>> = _payloadsState.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    override val loadingState = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState = _errorState.asStateFlow()

    fun publishFavoriteStreams() {
        viewModelScope.launch {
            repository.observeFavoriteStreams()
                .onStart { _loadingState.emit(true) }
                .catch {
                    _errorState.emit(it.message ?: "Error while loading favorite payloads.")
                    _loadingState.emit(false)
                }
                .collect { payloads ->
                    _loadingState.emit(false)
                    val sortedPayload = payloads.sortedByDescending { it.id }
                    _payloadsState.emit(sortedPayload).also {
                        println("Favorite payloads - ${payloads.size}")
                    }
                }
        }
    }
}