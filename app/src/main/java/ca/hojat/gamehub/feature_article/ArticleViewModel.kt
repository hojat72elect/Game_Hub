package ca.hojat.gamehub.feature_article

import androidx.lifecycle.SavedStateHandle
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.base.BaseViewModel
import ca.hojat.gamehub.core.providers.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

const val PARAM_IMAGE_URL = "image-url"
const val PARAM_TITLE = "title"
const val PARAM_LEDE = "lede"
const val PARAM_PUBLICATION_DATE = "publication-date"
const val PARAM_ARTICLE_URL = "article-url"
const val PARAM_BODY = "body"

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(createInitialUiState())

    private val currentUiState: ArticleUiState
        get() = _uiState.value

    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    private fun createInitialUiState(): ArticleUiState {
        return ArticleUiState(
            imageUrl = savedStateHandle.get<String>(PARAM_IMAGE_URL),
            title = savedStateHandle.get<String>(PARAM_TITLE) ?: "",
            lede = savedStateHandle.get<String>(PARAM_LEDE) ?: "",
            publicationDate = savedStateHandle.get<String>(PARAM_PUBLICATION_DATE) ?: "",
            articleUrl = savedStateHandle.get<String>(PARAM_ARTICLE_URL) ?: "",
            body = savedStateHandle.get<String>(PARAM_BODY) ?: "",
        )
    }

    fun onShareButtonClicked() {
        val textToShare = stringProvider.getString(
            R.string.text_sharing_message_template,
            stringProvider.getString(R.string.news_article),
            currentUiState.articleUrl
        )

        dispatchCommand(ArticleCommand.ShareText(textToShare))
    }

    fun onBackPressed() {
        route(ArticleRoute.Back)
    }

}