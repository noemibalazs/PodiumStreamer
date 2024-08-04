package base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirego.konnectivity.Konnectivity
import com.mirego.konnectivity.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseViewModel(private val konnectivity: Konnectivity) : ViewModel() {

    private var _networkState = MutableStateFlow(false)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    abstract val loadingState: StateFlow<Boolean>
    abstract val errorState: StateFlow<String>

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

    private fun onNetworkStateChanged(isActive: Boolean) {
        viewModelScope.launch {
            _networkState.emit(isActive)
        }
    }
}