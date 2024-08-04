package util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import com.fleeksoft.ksoup.Ksoup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.label_lazy_column_tag
import podiumstreamer.composeapp.generated.resources.label_progress_indicator_tag
import podiumstreamer.composeapp.generated.resources.label_user_avatar

fun showSnackBar(snackBarHostState: SnackbarHostState, message: String, scope: CoroutineScope) {
    scope.launch {
        snackBarHostState.showSnackbar(
            duration = SnackbarDuration.Short,
            message = message
        )
    }
}

@Composable
fun ConstraintLayoutScope.ProgressIndicator(reference: ConstrainedLayoutReference, modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .constrainAs(reference) {
                linkTo(parent.top, parent.bottom)
                linkTo(parent.start, parent.end)
            }
            .testTag(stringResource(Res.string.label_progress_indicator_tag)),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        strokeWidth = 3.dp
    )
}

@Composable
fun <T> ConstraintLayoutScope.PodiumLazyColumn(
    payloads: List<T>,
    lazyState: LazyListState,
    reference: ConstrainedLayoutReference,
    onPayloadData: @Composable (T) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 90.dp),
        modifier = modifier
            .constrainAs(reference) {
                linkTo(parent.start, parent.end)
                linkTo(parent.top, parent.bottom)
                height = Dimension.fillToConstraints
            }
            .testTag(stringResource(Res.string.label_lazy_column_tag)),
        state = lazyState
    ) {

        items(
            items = payloads,
            key = { it.hashCode() }
        ) { payloadData ->

            onPayloadData.invoke(payloadData)
        }
    }
}

@Composable
fun RowScope.StreamUser(userName: String, modifier: Modifier) {
    Text(
        text = userName,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp, start = 8.dp).align(Alignment.CenterVertically),
        textAlign = TextAlign.Justify,
        style = MaterialTheme.typography.headlineMedium,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun StreamImage(avatar: String, modifier: Modifier) {
    AsyncImage(
        model = avatar,
        contentDescription = stringResource(Res.string.label_user_avatar),
        modifier = modifier
            .size(width = 60.dp, height = 60.dp)
            .padding(top = 8.dp, end = 8.dp, start = 8.dp)
            .clip(CircleShape)
    )
}

@Composable
fun StreamContent(content: String, modifier: Modifier) {
    Text(
        text = Ksoup.parse(content).text(),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        textAlign = TextAlign.Justify,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    )
}

@Composable
fun StreamUrl(url: String, modifier: Modifier) {
    Text(
        text = url,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic
    )
}