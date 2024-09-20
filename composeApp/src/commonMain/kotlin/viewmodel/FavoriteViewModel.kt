package viewmodel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.mirego.konnectivity.Konnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.FavoriteStream
import repository.PayloadRepository

class FavoriteViewModel(
    konnectivity: Konnectivity,
    private val repository: PayloadRepository,
) : BaseViewModel<FavoriteStream>(konnectivity) {

    private var _payloadsState = MutableStateFlow(emptyList<FavoriteStream>())
    override val payloadsState: StateFlow<List<FavoriteStream>> = _payloadsState.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    override val loadingState = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState = _errorState.asStateFlow()

    init {
        publishFavoriteStreams()
    }

    private fun publishFavoriteStreams() {
        viewModelScope.launch {
            _loadingState.emit(true)

            repository.observeFavoriteStreams()
                .catch {
                    _errorState.emit(it.message ?: "Error while loading favorite payloads.")
                    _loadingState.emit(false)
                }
                .collectLatest { payloads ->
                    _loadingState.emit(false)
                    val sortedPayload = payloads.sortedByDescending { it.id }
                    _payloadsState.emit(sortedPayload).also {
                        println("Favorite payloads - ${payloads.size}")
                    }
                }
        }
    }
}