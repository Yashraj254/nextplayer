package dev.anilbeesetti.nextplayer.feature.videopicker.screens.mediaFolder

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.anilbeesetti.nextplayer.core.common.extensions.prettyName
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.components.NextTopAppBar
import dev.anilbeesetti.nextplayer.core.ui.designsystem.NextIcons
import dev.anilbeesetti.nextplayer.feature.videopicker.composables.VideosListFromState
import dev.anilbeesetti.nextplayer.feature.videopicker.screens.VideosState
import java.io.File

@Composable
fun MediaPickerFolderRoute(
    viewModel: MediaPickerFolderViewModel = hiltViewModel(),
    onVideoClick: (uri: Uri) -> Unit,
    onNavigateUp: () -> Unit
) {
    // The app experiences jank when videosState updates before the initial render finishes.
    // By adding Lifecycle.State.RESUMED, we ensure that we wait until the first render completes.
    val videosState by viewModel.videos.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)

    val deleteIntentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {}
    )

    MediaPickerFolderScreen(
        folderPath = viewModel.folderPath,
        videosState = videosState,
        onVideoClick = onVideoClick,
        onNavigateUp = onNavigateUp,
        onDeleteVideoClick = { viewModel.deleteVideos(listOf(it), deleteIntentSenderLauncher) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaPickerFolderScreen(
    folderPath: String,
    videosState: VideosState,
    onNavigateUp: () -> Unit,
    onVideoClick: (Uri) -> Unit,
    onDeleteVideoClick: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var searchBarActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val textFieldFocusRequester = remember { FocusRequester() }

    Column {
        NextTopAppBar(
            title = if (!searchBarActive) File(folderPath).prettyName else "",
            navigationIcon = {
                if (!searchBarActive) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = NextIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                }
            },
            actions = {
                if (!searchBarActive) {
                    IconButton(onClick = {
                        searchBarActive = true
                    }) {
                        Icon(
                            imageVector = NextIcons.Search,
                            contentDescription = stringResource(id = R.string.search_videos)
                        )
                    }
                } else {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .focusRequester(textFieldFocusRequester),
                        value = title,
                        onValueChange = { title = it },

                        trailingIcon = {
                            IconButton(onClick = {
                                searchBarActive = false
                                focusManager.clearFocus()
                                title = ""
                            }) {
                                Icon(
                                    imageVector = NextIcons.Close,
                                    contentDescription = stringResource(
                                        id = R.string.close_search_bar
                                    )
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.search))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    LaunchedEffect(key1 = searchBarActive) {
                        textFieldFocusRequester.requestFocus()
                    }
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VideosListFromState(
                videosState = videosState,
                onVideoClick = onVideoClick,
                onDeleteVideoClick = onDeleteVideoClick,
                title = title
            )
        }
    }
}
