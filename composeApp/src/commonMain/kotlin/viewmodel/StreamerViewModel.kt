package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.mirego.konnectivity.Konnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.Event
import model.EventType
import model.PayloadData
import network.KTorDataSource
import repository.PayloadRepository
import util.toFavoriteStream

class StreamerViewModel(
    private val ktorDataSource: KTorDataSource,
    private val repository: PayloadRepository,
    konnectivity: Konnectivity
) : BaseViewModel(konnectivity) {

    private var _payloadsState = MutableStateFlow(emptyList<PayloadData>())
    val payloadsState = _payloadsState.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    override val loadingState = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState = _errorState.asStateFlow()

    var searchTerm by mutableStateOf("")

    fun publishPayloads() {
        viewModelScope.launch {
            repository.observeStreams().collect { payloads ->
                val sortedPayload = payloads.sortedByDescending { it.id }
                _payloadsState.emit(sortedPayload).also {
                    println("Publish payloads - ${payloads.size}")
                }
            }
        }
    }

    fun fetchPublicTimelines(query: String) {
        viewModelScope.launch {

            _loadingState.emit(true)
            _payloadsState.emit(emptyList())

            repository.nukeStreams()

            ktorDataSource.observePayloads(query)
                .catch {
                    _errorState.emit(it.message ?: "Error while fetching events")
                    _loadingState.emit(false)
                }
                .collectLatest { event ->
                    handleEventResponse(event)
                    println("Event data: $event")
                }
        }
    }

    fun reFetchPublicTimelines() {
        viewModelScope.launch {
            _errorState.emit("")

            ktorDataSource.observePayloads(searchTerm)
                .catch {
                    _errorState.emit(it.message ?: "Error while fetching events")
                }
                .collectLatest {
                    handleEventResponse(it)
                    println("Event data: $it")
                }
        }
    }

    private fun handleEventResponse(event: Event) {
        viewModelScope.launch {

            _loadingState.emit(false)

            when (event.type == EventType.DELETE) {
                true -> repository.observeStreams().collect { payloads ->
                    payloads.find { payload -> payload.id == event.id }?.let { payload ->
                        repository.deleteStream(payload.id)
                        println("On Event Received! Stream payload -: $payload")
                    }
                }

                false -> event.payload?.let { payload ->
                    repository.saveStream(payload)
                    println("On Event Received! Stream payload -: $payload")
                }
            }
        }
    }

    fun onSearchTermChanged(query: String) {
        searchTerm = query
    }

    fun saveFavoriteStream(payloadData: PayloadData) {
        viewModelScope.launch {
            repository.saveFavoriteStream(payloadData.toFavoriteStream())
        }
    }
}