package com.example.adopcion_mascotas.core.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.huellas.app.core.util.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Escucha los eventos del ViewModel: muestra Snackbars y ejecuta la navegación en Success.
 */
@Composable
fun CollectUiEvents(
    events: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
    onSuccess: () -> Unit = {}
) {
    val currentOnSuccess by rememberUpdatedState(onSuccess)
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    launch { snackbarHostState.showSnackbar(event.message) }
                }
                UiEvent.Success -> currentOnSuccess()
            }
        }
    }
}
