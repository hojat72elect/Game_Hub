package ca.hojat.gamehub.feature_news.presentation.widgets

import androidx.compose.runtime.Immutable
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState

@Immutable
data class NewsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val news: List<NewsItemUiModel> = emptyList(),
) {

    val finiteUiState: FiniteUiState
        get() = when {
            isInEmptyState -> FiniteUiState.EMPTY
            isLoading -> FiniteUiState.LOADING
            isInSuccessState -> FiniteUiState.SUCCESS
            else -> error("Unknown gaming news UI state.")
        }

    private val isInEmptyState: Boolean
        get() = (!isLoading && news.isEmpty())

    private val isInSuccessState: Boolean
        get() = news.isNotEmpty()

    fun toEmptyState(): NewsUiState {
        return copy(isLoading = false, news = emptyList())
    }

    fun toLoadingState(): NewsUiState {
        return copy(isLoading = true)
    }

    fun toSuccessState(
        news: List<NewsItemUiModel>
    ): NewsUiState {
        return copy(isLoading = false, news = news)
    }

    fun enableRefreshing(): NewsUiState {
        return copy(isRefreshing = true)
    }

    fun disableRefreshing(): NewsUiState {
        return copy(isRefreshing = false)
    }

}
