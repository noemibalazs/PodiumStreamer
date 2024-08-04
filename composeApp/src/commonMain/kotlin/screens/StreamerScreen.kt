package screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.PayloadData
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin
import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.label_favorite_stream
import podiumstreamer.composeapp.generated.resources.label_no_internet_connection
import podiumstreamer.composeapp.generated.resources.label_search_hint
import podiumstreamer.composeapp.generated.resources.label_search_text_tag
import podiumstreamer.composeapp.generated.resources.label_timeline
import theme.PhilosopherFontFamily
import util.PodiumLazyColumn
import util.ProgressIndicator
import util.StreamContent
import util.StreamImage
import util.StreamUrl
import util.StreamUser
import util.showSnackBar
import viewmodel.StreamerViewModel

@Composable
fun StreamerScreen(snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val viewModel: StreamerViewModel = viewModel { getKoin().get() }

    val payloadsState by viewModel.payloadsState.collectAsStateWithLifecycle()
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            launch {
                viewModel.monitorNetworkState(scope)
                viewModel.publishPayloads()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        SearchTextField(
            searchTerm = viewModel.searchTerm,
            onSearchTermChanged = viewModel::onSearchTermChanged,
            onGetPublicTimelines = viewModel::fetchPublicTimelines,
            isActiveNetwork = networkState,
            snackBarHostState = snackBarHostState,
            modifier = modifier
        )

        StreamerContent(
            payloads = payloadsState,
            isLoading = loadingState,
            isActiveNetwork = networkState,
            snackBarHostState = snackBarHostState,
            onSaveFavoriteStream = viewModel::saveFavoriteStream,
            modifier = modifier
        )

        if (errorMessage.isNotBlank()) {

            var stateValue by remember { mutableStateOf(false) }

            scope.launch {
                snackBarHostState.showSnackbar(
                    duration = SnackbarDuration.Short,
                    message = errorMessage
                )
                stateValue = true
            }

            if (stateValue && networkState) viewModel.reFetchPublicTimelines()
        }
    }
}

@Composable
private fun SearchTextField(
    searchTerm: String,
    onSearchTermChanged: (String) -> Unit,
    onGetPublicTimelines: (String) -> Unit,
    isActiveNetwork: Boolean,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val message = stringResource(Res.string.label_no_internet_connection)

    LaunchedEffect(Unit) {
        delay(60)
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester = focusRequester)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .testTag(stringResource(Res.string.label_search_text_tag)),
        value = searchTerm,
        onValueChange = { query ->
            onSearchTermChanged(query)
        },
        textStyle = TextStyle(
            fontFamily = PhilosopherFontFamily(),
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp,
            color = Color.Black
        ),
        label = {
            Text(
                text = stringResource(Res.string.label_timeline),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        placeholder = {
            Text(
                text = stringResource(Res.string.label_search_hint),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = true,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.None
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            when (isActiveNetwork) {
                true -> onGetPublicTimelines(searchTerm)
                else -> showSnackBar(snackBarHostState = snackBarHostState, message = message, scope = scope)
            }
        }),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                modifier = modifier.clickable {
                    keyboardController?.hide()
                    onSearchTermChanged("")
                }
            )
        }
    )
}

@Composable
private fun StreamerContent(
    payloads: List<PayloadData>,
    isLoading: Boolean,
    isActiveNetwork: Boolean,
    snackBarHostState: SnackbarHostState,
    onSaveFavoriteStream: (PayloadData) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

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
                modifier = modifier,
                onPayloadData = { payloadData ->
                    if (isActiveNetwork && !lazyListState.isScrollInProgress) {
                        scope.launch {
                            lazyListState.scrollToItem(0)
                        }
                    }

                    StreamerItemRow(
                        payload = payloadData,
                        snackBarHostState = snackBarHostState,
                        onSaveFavoriteStream = onSaveFavoriteStream,
                        modifier = modifier
                    )
                }
            )
        }
    }
}

@Composable
private fun StreamerItemRow(
    payload: PayloadData,
    snackBarHostState: SnackbarHostState,
    onSaveFavoriteStream: (PayloadData) -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val message = stringResource(Res.string.label_favorite_stream)

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
                    showSnackBar(snackBarHostState = snackBarHostState, message = message, scope = scope)
                    onSaveFavoriteStream.invoke(payload)
                }
        ) {
            Column {

                Row {

                    StreamImage(payload.account.avatar, modifier)

                    StreamUser(payload.account.username, modifier)
                }

                StreamContent(payload.content, modifier)

                StreamUrl(payload.url, modifier)
            }
        }
    }
}