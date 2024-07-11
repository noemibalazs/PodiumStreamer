package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirego.konnectivity.Konnectivity
import com.mirego.konnectivity.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import model.Event
import model.EventType
import model.PayloadData
import network.KTorDataSource
import repository.PayloadRepository

class PayloadViewModel(
    private val ktorDataSource: KTorDataSource,
    private val repository: PayloadRepository,
    private val konnectivity: Konnectivity
) : ViewModel() {

    private var _payloadsState = MutableStateFlow(emptyList<PayloadData>())
    val payloadsState = _payloadsState.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    val errorState = _errorState.asStateFlow()

    private var _networkState = MutableStateFlow(false)
    val networkState = _networkState.asStateFlow()

    var searchTerm by mutableStateOf("")

    fun publishPayloads() {
        viewModelScope.launch {
            repository.observePayloads().collect { payloads ->
                val sortedPayload = payloads.sortedByDescending { it.id }
                _payloadsState.emit(sortedPayload).also {
                    println("Publish payloads - ${payloads.size}")
                }
            }
        }
    }

    fun monitorNetworkState(scope: CoroutineScope) {
        konnectivity.networkState
            .onEach { networkState ->
                when (networkState) {
                    NetworkState.Unreachable -> onNetworkStateChanged(false)
                    else -> onNetworkStateChanged(true)
                }.also {
                    println("Network state is active: $networkState")
                }
            }.launchIn(scope)
    }

    fun fetchPublicTimelines(query: String) {
        viewModelScope.launch {

            _loadingState.emit(true)
            _payloadsState.emit(emptyList())

            repository.clearDataBase()

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
                true -> repository.observePayloads().collect { payloads ->
                    payloads.find { payload -> payload.id == event.id }?.let { payload ->
                        repository.deletePayloadData(payload.id)
                        println("On Event Received! Stream payload -: $payload")
                    }
                }

                false -> event.payload?.let { payload ->
                    repository.savePayload(payload)
                    println("On Event Received! Stream payload -: $payload")
                }
            }
        }
    }

    fun onSearchTermChanged(query: String) {
        searchTerm = query
    }

    private fun onNetworkStateChanged(isActive: Boolean) {
        viewModelScope.launch {
            _networkState.emit(isActive)
        }
    }
}