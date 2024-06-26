package com.mdev.chatapp.ui.common.nav_drawer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdev.chatapp.domain.repository.remote.AuthRepository
import com.mdev.chatapp.ui.navgraph.Route
import com.mdev.chatapp.util.CheckInternConnection.Companion.isInternetConnected
import com.mdev.chatapp.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigateDrawerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
): ViewModel(){
    private val uiEventChannel = Channel<UIEvent>()
    val uiEvent = uiEventChannel.receiveAsFlow()

    private val _connectionStateLiveData = MutableLiveData<Boolean>()
    val connectionStateLiveData: LiveData<Boolean> = _connectionStateLiveData

    init {
        viewModelScope.launch {
            while (true) {
                val isConnected = isInternetConnected(context)
                _connectionStateLiveData.postValue(isConnected)
                delay(5000)
            }
        }
    }

    fun onEvent(event: NavDrawerUIEvent) {
        when(event) {
            is NavDrawerUIEvent.Logout -> {
                logout()
            }
            is NavDrawerUIEvent.Home -> {
                sendUIEvent(UIEvent.NavigateTo(Route.HomeScreen))
            }
            is NavDrawerUIEvent.Profile -> {
                sendUIEvent(UIEvent.NavigateTo(Route.ProfileScreen))
            }
            is NavDrawerUIEvent.NewChat -> {
                sendUIEvent(UIEvent.NavigateTo(Route.ChatScreen))
            }
            is NavDrawerUIEvent.History -> {
                sendUIEvent(UIEvent.NavigateTo(Route.HistoryScreen))
            }
            is NavDrawerUIEvent.About -> {
                sendUIEvent(UIEvent.NavigateTo(Route.AboutScreen))
            }
            is NavDrawerUIEvent.Settings -> {
                sendUIEvent(UIEvent.NavigateTo(Route.SettingsScreen))
            }
            is NavDrawerUIEvent.Back -> {
                sendUIEvent(UIEvent.Back)
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            uiEventChannel.send(UIEvent.Logout)
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            uiEventChannel.send(event)
        }
    }
}