package ca.hojat.gamehub.feature_image_viewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.extensions.fromCsv
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.common_ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

const val PARAM_GAME_NAME = "game-name"
const val PARAM_TITLE = "title"
const val PARAM_INITIAL_POSITION = "initial-position"
const val PARAM_IMAGE_URLS = "image-urls"

const val KEY_SELECTED_POSITION = "selected_position"

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    // can be artwork, cover, or screenshot
    private val imageType: String = savedStateHandle.get<String>(PARAM_TITLE)
        ?: stringProvider.getString(R.string.image_viewer_default_toolbar_title)

    // Name of the game will only be used for notification title, when user downloads this image.
    private val gameName: String = savedStateHandle.get<String>(PARAM_GAME_NAME) ?: ""

    private val _uiState = MutableStateFlow(createInitialUiState())

    private val currentUiState: ImageViewerUiState
        get() = _uiState.value

    val uiState: StateFlow<ImageViewerUiState> = _uiState.asStateFlow()

    init {

        _uiState.update {
            it.copy(
                selectedImageUrlIndex = getSelectedPosition(),
                imageUrls = parseImageUrls(),
            )
        }

        observeSelectedPositionChanges()
    }

    private fun createInitialUiState(): ImageViewerUiState {
        return ImageViewerUiState(
            gameName = "",
            toolbarTitle = "",
            imageUrls = emptyList(),
            selectedImageUrlIndex = 0,
        )
    }

    private fun getSelectedPosition(): Int {
        return savedStateHandle[KEY_SELECTED_POSITION]
            ?: checkNotNull(savedStateHandle.get<Int>(PARAM_INITIAL_POSITION))
    }

    private fun parseImageUrls(): List<String> {
        return savedStateHandle.get<String>(PARAM_IMAGE_URLS)
            ?.fromCsv()
            ?: error("No image urls provided.")
    }

    private fun observeSelectedPositionChanges() {
        uiState
            .map { it.selectedImageUrlIndex }
            .distinctUntilChanged()
            .onEach { selectedImageUrlIndex ->
                _uiState.update { it.copy(toolbarTitle = updateToolbarTitle()) }
                savedStateHandle[KEY_SELECTED_POSITION] = selectedImageUrlIndex
            }
            .launchIn(viewModelScope)
    }

    private fun updateToolbarTitle(): String {
        if (currentUiState.imageUrls.size == 1) return imageType

        return stringProvider.getString(
            R.string.image_viewer_toolbar_title_template,
            imageType,
            (currentUiState.selectedImageUrlIndex + 1),
            currentUiState.imageUrls.size
        )
    }

    fun onShareButtonClicked() {
        val currentImageUrl = currentUiState.imageUrls[currentUiState.selectedImageUrlIndex]
        val textToShare = stringProvider.getString(
            R.string.text_sharing_message_template,
            stringProvider.getString(R.string.image),
            currentImageUrl
        )

        dispatchCommand(ImageViewerCommand.ShareText(textToShare))
    }

    fun onDownloadButtonClicked() {
        val url = currentUiState.imageUrls[currentUiState.selectedImageUrlIndex]
        val fileName = "${gameName}_$imageType${currentUiState.selectedImageUrlIndex + 1}.jpg"

        dispatchCommand(ImageViewerCommand.DownloadFile(url, gameName, fileName))
    }

    fun onImageChanged(imageIndex: Int) {
        _uiState.update { it.copy(selectedImageUrlIndex = imageIndex) }
    }

    fun onBackPressed() {
        route(ImageViewerRoute.Back)
    }
}
