package ca.hojat.gamehub.feature_info.presentation.widgets.main

import androidx.compose.runtime.Immutable
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState

@Immutable
data class InfoScreenUiState(
    val isLoading: Boolean,
    val game: InfoScreenUiModel?,
) {

    val finiteUiState: FiniteUiState
        get() = when {
            isInEmptyState -> FiniteUiState.EMPTY
            isInLoadingState -> FiniteUiState.LOADING
            isInSuccessState -> FiniteUiState.SUCCESS
            else -> error("Unknown game info UI state.")
        }

    private val isInEmptyState: Boolean
        get() = (!isLoading && (game == null))

    private val isInLoadingState: Boolean
        get() = (isLoading && (game == null))

    private val isInSuccessState: Boolean
        get() = (game != null)

    fun toEmptyState(): InfoScreenUiState {
        return copy(isLoading = false, game = null)
    }

    fun toLoadingState(): InfoScreenUiState {
        return copy(isLoading = true)
    }

    fun toSuccessState(game: InfoScreenUiModel): InfoScreenUiState {
        return copy(isLoading = false, game = game)
    }

}
