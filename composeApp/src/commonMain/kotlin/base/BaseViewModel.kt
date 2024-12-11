package base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirego.konnectivity.Konnectivity
import com.mirego.konnectivity.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseViewModel<T>(private val konnectivity: Konnectivity) : ViewModel() {

    private var _networkState = MutableStateFlow(false)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    abstract val payloadsState: StateFlow<List<T>>
    abstract val loadingState: StateFlow<Boolean>
    abstract val errorState: StateFlow<String>

    init {
        monitorNetworkState()
    }

    private fun monitorNetworkState() {
        konnectivity.networkState
            .onEach { networkState ->
                when (networkState) {
                    is NetworkState.Reachable -> onNetworkStateChanged(true)
                    else -> onNetworkStateChanged(false)
                }
            }.launchIn(viewModelScope)
    }

    private fun onNetworkStateChanged(isActive: Boolean) {
        viewModelScope.launch {
            _networkState.emit(isActive)
            println("Network state is active: $isActive")
        }
    }
}