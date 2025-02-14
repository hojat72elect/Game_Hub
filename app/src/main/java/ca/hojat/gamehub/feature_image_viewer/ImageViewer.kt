package ca.hojat.gamehub.feature_image_viewer

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.hojat.gamehub.R
import ca.hojat.gamehub.common_ui.CommandsHandler
import ca.hojat.gamehub.common_ui.LocalDownloader
import ca.hojat.gamehub.common_ui.LocalNetworkStateProvider
import ca.hojat.gamehub.common_ui.LocalTextSharer
import ca.hojat.gamehub.common_ui.RoutesHandler
import ca.hojat.gamehub.common_ui.base.events.Route
import ca.hojat.gamehub.common_ui.images.defaultImageRequest
import ca.hojat.gamehub.common_ui.theme.GameHubTheme
import ca.hojat.gamehub.common_ui.theme.navBar
import ca.hojat.gamehub.common_ui.theme.statusBar
import ca.hojat.gamehub.common_ui.widgets.Info
import ca.hojat.gamehub.common_ui.widgets.toolbars.Toolbar
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State
import coil.size.Size
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState

private const val ZoomScaleMin = 0.5f
private const val ZoomScaleMax = 5f
private const val ZoomOverSnapScaleMin = 1f
private const val ZoomOverSnapScaleMax = 3f

@Composable
fun ImageViewer(onRoute: (Route) -> Unit) {
    ImageViewer(
        viewModel = hiltViewModel(),
        onRoute = onRoute,
    )
}

@Composable
private fun ImageViewer(
    viewModel: ImageViewerViewModel,
    onRoute: (Route) -> Unit,
) {
    val textSharer = LocalTextSharer.current
    val context = LocalContext.current
    val downloader = LocalDownloader.current

    CommandsHandler(viewModel = viewModel) { command ->
        when (command) {
            is ImageViewerCommand.ShareText -> {
                textSharer.share(context, command.text)
            }
            is ImageViewerCommand.DownloadFile -> {
                downloader.downloadFile(
                    url = command.url,
                    notificationTitle = command.gameName,
                    fileName = command.fileName
                )
            }
        }
    }
    RoutesHandler(viewModel = viewModel, onRoute = onRoute)
    ImageViewer(
        uiState = viewModel.uiState.collectAsState().value,
        onBackPressed = viewModel::onBackPressed,
        onShareBtnClicked = viewModel::onShareButtonClicked,
        onImageChanged = viewModel::onImageChanged,
        onDownloadBtnClicked = viewModel::onDownloadButtonClicked,
        onDismiss = viewModel::onBackPressed,
    )
}

@Composable
private fun ImageViewer(
    uiState: ImageViewerUiState,
    onBackPressed: () -> Unit,
    onShareBtnClicked: () -> Unit,
    onDownloadBtnClicked: () -> Unit,
    onImageChanged: (imageIndex: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    SystemBarsColorHandler()
    BackHandler(onBack = onBackPressed)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black,
        contentColor = Color.White,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Pager(
                uiState = uiState,
                modifier = Modifier.matchParentSize(),
                onImageChanged = onImageChanged,
                onDismiss = onDismiss,
            )

            Toolbar(
                title = uiState.toolbarTitle,
                modifier = Modifier.align(Alignment.TopCenter),
                contentPadding = WindowInsets.statusBars
                    .only(WindowInsetsSides.Vertical + WindowInsetsSides.Horizontal)
                    .asPaddingValues(),
                backgroundColor = GameHubTheme.colors.statusBar,
                contentColor = LocalContentColor.current,
                elevation = 0.dp,
                backButtonIcon = painterResource(R.drawable.arrow_left),
                firstButtonIcon = painterResource(R.drawable.share_variant),
                secondButtonIcon = painterResource(R.drawable.download_24),
                onBackButtonClick = onBackPressed,
                onFirstButtonClick = onShareBtnClicked,
                onSecondButtonClick = onDownloadBtnClicked
            )
        }
    }
}

@Composable
fun SystemBarsColorHandler() {
    val systemUiController = rememberSystemUiController()
    val defaultStatusBarColor = GameHubTheme.colors.statusBar
    val defaultNavigationBarColor = GameHubTheme.colors.navBar

    DisposableEffect(defaultStatusBarColor, defaultNavigationBarColor) {
        // We want to make the system bars translucent when viewing images
        with(systemUiController) {
            // Making the status bar transparent causes it to use the color
            // of the toolbar (which uses the status bar color)
            setStatusBarColor(Color.Transparent)
            // We want the color of the navigation bar to be
            // the same as the color of the status bar
            setNavigationBarColor(defaultStatusBarColor)
        }

        onDispose {
            with(systemUiController) {
                setStatusBarColor(defaultStatusBarColor)
                setNavigationBarColor(defaultNavigationBarColor)
            }
        }
    }
}


@Composable
private fun Pager(
    uiState: ImageViewerUiState,
    modifier: Modifier,
    onImageChanged: (imageIndex: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = uiState.selectedImageUrlIndex)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { pageIndex -> onImageChanged(pageIndex) }
    }

    HorizontalPager(
        count = uiState.imageUrls.size,
        modifier = modifier,
        state = pagerState,
        itemSpacing = GameHubTheme.spaces.spacing_2_0,
    ) { pageIndex ->
        ImageItem(
            imageUrl = uiState.imageUrls[pageIndex],
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun ImageItem(
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var imageState by remember { mutableStateOf<State>(State.Empty) }
        val zoomableState = rememberZoomableState(
            minScale = ZoomScaleMin,
            maxScale = ZoomScaleMax,
            overZoomConfig = OverZoomConfig(
                minSnapScale = ZoomOverSnapScaleMin,
                maxSnapScale = ZoomOverSnapScaleMax,
            ),
        )

        if (imageState is State.Error) {
            Info(
                icon = painterResource(R.drawable.alert_circle_outline),
                title = stringResource(
                    if (!LocalNetworkStateProvider.current.isNetworkAvailable) {
                        R.string.error_no_network_message
                    } else {
                        R.string.error_unknown_message
                    }
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = GameHubTheme.spaces.spacing_7_5),
            )
        }

        // For Zoomable to work, a couple of rules have to be followed:
        // - size(Size.ORIGINAL) has to be specified in the Coil request
        // - Modifier.aspectRatio() set
        Zoomable(
            state = zoomableState,
            dismissGestureEnabled = true,
            onDismiss = {
                onDismiss()
                true
            },
        ) {
            val aspectRatioModifier = if (imageState is State.Success) {
                val size = checkNotNull(imageState.painter).intrinsicSize
                Modifier.aspectRatio(size.width / size.height)
            } else {
                Modifier
            }

            AsyncImage(
                model = defaultImageRequest(imageUrl) {
                    size(Size.ORIGINAL)
                },
                contentDescription = null,
                modifier = Modifier
                    .then(aspectRatioModifier)
                    .fillMaxSize(),
                onState = { state ->
                    imageState = state
                },
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ImageViewerPreview() {
    GameHubTheme {
        ImageViewer(
            uiState = ImageViewerUiState(
                gameName = "",
                toolbarTitle = "Image",
                imageUrls = emptyList(),
                selectedImageUrlIndex = 0,
            ),
            onBackPressed = {},
            onShareBtnClicked = {},
            onImageChanged = {},
            onDownloadBtnClicked = {},
            onDismiss = {},
        )
    }
}
