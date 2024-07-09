import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.fleeksoft.ksoup.Ksoup
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.label_icon_content_description
import podiumstreamer.composeapp.generated.resources.label_lazy_column_tag
import podiumstreamer.composeapp.generated.resources.label_no_internet_connection
import podiumstreamer.composeapp.generated.resources.label_podium_streamer
import podiumstreamer.composeapp.generated.resources.label_progress_indicator_tag
import podiumstreamer.composeapp.generated.resources.label_search_hint
import podiumstreamer.composeapp.generated.resources.label_search_text_tag
import podiumstreamer.composeapp.generated.resources.label_timeline
import podiumstreamer.composeapp.generated.resources.label_user_avatar
import podiumstreamer.composeapp.generated.resources.logo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.PayloadData
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin
import theme.PhilosopherFontFamily
import theme.PhilosopherTypography
import theme.StreamerTheme
import viewmodel.PayloadViewModel

@Composable
@Preview
fun App() {

    val snackbarHostState = remember { SnackbarHostState() }
    val modifier: Modifier = Modifier

    StreamerTheme {

        PreComposeApp {

            Scaffold(
                topBar = {
                    StreamerAppBar(
                        title = stringResource(Res.string.label_podium_streamer),
                        contentDescription = stringResource(Res.string.label_icon_content_description),
                        modifier = modifier
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                StreamerScreen(snackBarHostState = snackbarHostState, modifier = modifier)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerAppBar(title: String, contentDescription: String, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = PhilosopherFontFamily(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = contentDescription,
                modifier = modifier.size(32.dp).padding(start = 12.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun StreamerScreen(snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val viewModel: PayloadViewModel = viewModel { getKoin().get() }

    val payloadsState by viewModel.payloadsState.collectAsStateWithLifecycle()
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.publishPayloads()
            }

            launch {
                viewModel.monitorNetworkState(scope)
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

        ScreenContent(
            payloads = payloadsState,
            isLoading = loadingState,
            isActiveNetwork = networkState,
            snackBarHostState = snackBarHostState,
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

            if (stateValue) viewModel.reFetchPublicTimelines()
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
                fontFamily = PhilosopherFontFamily(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        placeholder = {
            Text(
                text = stringResource(Res.string.label_search_hint),
                fontFamily = PhilosopherFontFamily(),
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
            onGetPublicTimelines(searchTerm)
            when (isActiveNetwork) {
                true -> onGetPublicTimelines(searchTerm)
                else -> ShowSnackBar(snackBarHostState = snackBarHostState, message = message, scope = scope)
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
private fun ScreenContent(payloads: List<PayloadData>, isLoading: Boolean, isActiveNetwork: Boolean, snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (progressIndicator, column) = createRefs()

        when (isLoading) {
            true ->
                CircularProgressIndicator(
                    modifier = modifier
                        .constrainAs(progressIndicator) {
                            linkTo(parent.top, parent.bottom)
                            linkTo(parent.start, parent.end)
                        }
                        .testTag(stringResource(Res.string.label_progress_indicator_tag)),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    strokeWidth = 3.dp
                )

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 20.dp),
                    modifier = modifier
                        .constrainAs(column) {
                            linkTo(parent.start, parent.end)
                            linkTo(parent.top, parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                        .testTag(stringResource(Res.string.label_lazy_column_tag)),
                    state = lazyListState
                ) {

                    items(
                        items = payloads,
                        key = { it.id }
                    ) { stream ->
                        if (isActiveNetwork && !lazyListState.isScrollInProgress) {
                            scope.launch {
                                lazyListState.scrollToItem(0)
                            }
                        }

                        StreamerItemRow(
                            payload = stream,
                            isActiveNetwork = isActiveNetwork,
                            snackBarHostState = snackBarHostState,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamerItemRow(payload: PayloadData, isActiveNetwork: Boolean, snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {

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
                        else -> ShowSnackBar(snackBarHostState = snackBarHostState, message = message, scope = scope)
                    }
                }
        ) {
            Column {

                Row {

                    AsyncImage(
                        model = payload.account.avatar,
                        contentDescription = stringResource(Res.string.label_user_avatar),
                        modifier = modifier
                            .size(width = 60.dp, height = 60.dp)
                            .padding(top = 8.dp, end = 8.dp, start = 8.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = payload.account.username,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 8.dp, start = 8.dp).align(Alignment.CenterVertically),
                        textAlign = TextAlign.Justify,
                        style = PhilosopherTypography().headlineMedium,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = Ksoup.parse(payload.content).text(),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 6.dp),
                    textAlign = TextAlign.Justify,
                    style = PhilosopherTypography().bodyMedium,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = PhilosopherFontFamily()
                )

                Text(
                    text = payload.url,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.End,
                    style = PhilosopherTypography().bodyMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
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

private fun ShowSnackBar(snackBarHostState: SnackbarHostState, message: String, scope: CoroutineScope) {
    scope.launch {
        snackBarHostState.showSnackbar(
            duration = SnackbarDuration.Short,
            message = message
        )
    }
}
