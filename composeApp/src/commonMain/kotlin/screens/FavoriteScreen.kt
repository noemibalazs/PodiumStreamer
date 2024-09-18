package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.launch
import model.FavoriteStream
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin
import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.label_no_internet_connection
import util.PodiumLazyColumn
import util.ProgressIndicator
import util.StreamContent
import util.StreamImage
import util.StreamUrl
import util.StreamUser
import util.showSnackBar
import viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val viewModel: FavoriteViewModel = viewModel { getKoin().get() }

    val payloadState by viewModel.payloadsState.collectAsStateWithLifecycle()
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val errorMessageState by viewModel.errorState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.monitorNetworkState(scope)
            viewModel.publishFavoriteStreams()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        FavoriteContent(
            payloads = payloadState,
            isLoading = loadingState,
            isActiveNetwork = networkState,
            snackBarHostState = snackBarHostState,
            modifier = modifier
        )

        if (errorMessageState.isNotBlank()) {

            scope.launch {
                snackBarHostState.showSnackbar(
                    duration = SnackbarDuration.Short,
                    message = errorMessageState
                )
            }
        }
    }
}

@Composable
private fun FavoriteContent(
    payloads: List<FavoriteStream>,
    isLoading: Boolean,
    isActiveNetwork: Boolean,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (progressIndicator, lazyColumn) = createRefs()

        when (isLoading) {
            true -> ProgressIndicator(progressIndicator, modifier)
            else -> PodiumLazyColumn(
                payloads = payloads,
                lazyState = lazyListState,
                reference = lazyColumn,
                onPayloadData = { payload ->
                    FavoriteItemRow(
                        payload = payload,
                        isActiveNetwork = isActiveNetwork,
                        snackBarHostState = snackBarHostState,
                        modifier = modifier
                    )
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FavoriteItemRow(
    payload: FavoriteStream,
    isActiveNetwork: Boolean,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val message = stringResource(Res.string.label_no_internet_connection)

    var isWebViewDialogOpen by remember { mutableStateOf(false) }
    val onDismiss: (Boolean) -> Unit = { changed ->
        isWebViewDialogOpen = changed
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.large,
            modifier = modifier
                .padding(8.dp)
                .clickable {
                    when (isActiveNetwork) {
                        true -> isWebViewDialogOpen = true
                        else -> showSnackBar(snackBarHostState = snackBarHostState, message = message, scope = scope)
                    }
                }
        ) {
            Column {

                Row {

                    StreamImage(payload.avatar, modifier)

                    StreamUser(payload.username, modifier)
                }

                StreamContent(payload.content, modifier)

                StreamUrl(payload.url, modifier)
            }
        }
    }

    if (isWebViewDialogOpen) {
        StreamerLinkDialog(url = payload.url, onDismissRequest = onDismiss)
    }
}

@Composable
private fun StreamerLinkDialog(url: String, onDismissRequest: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = { onDismissRequest.invoke(false) }) {

        Card(
            modifier = modifier
                .background(
                    shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {

                StreamerWebView(url = url)
            }
        }
    }
}

@Composable
private fun StreamerWebView(url: String) {
    val webViewState = rememberWebViewState(url)

    WebView(
        state = webViewState,
        modifier = Modifier.fillMaxSize()
    )
}