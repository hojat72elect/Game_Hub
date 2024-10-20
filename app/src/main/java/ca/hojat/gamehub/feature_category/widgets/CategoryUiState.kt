package ca.hojat.gamehub.feature_category.widgets

import androidx.compose.runtime.Immutable
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState

@Immutable
data class CategoryUiState(
    val isLoading: Boolean,
    val title: String,
    val items: List<CategoryUiModel>,
) {
    val finiteUiState: FiniteUiState
        get() = when {
            isInEmptyState -> FiniteUiState.EMPTY
            isInLoadingState -> FiniteUiState.LOADING
            isInSuccessState -> FiniteUiState.SUCCESS
            else -> error("Unknown games category UI state.")
        }

    private val isInEmptyState: Boolean
        get() = (!isLoading && items.isEmpty())

    private val isInLoadingState: Boolean
        get() = (isLoading && items.isEmpty())

    private val isInSuccessState: Boolean
        get() = items.isNotEmpty()

    val isRefreshing: Boolean
        get() = (isLoading && items.isNotEmpty())

    fun enableLoading(): CategoryUiState {
        return copy(isLoading = true)
    }

    fun disableLoading(): CategoryUiState {
        return copy(isLoading = false)
    }

    fun toEmptyState(): CategoryUiState {
        return copy(isLoading = false, items = emptyList())
    }

    fun toSuccessState(
        games: List<CategoryUiModel>
    ): CategoryUiState {
        return copy(isLoading = false, items = games)
    }

    fun hasLoadedNewGames(): Boolean {
        return (!isLoading && items.isNotEmpty())
    }
}

@Immutable
data class CategoryUiModel(
    val id: Int,
    val title: String,
    val coverUrl: String?,
)
