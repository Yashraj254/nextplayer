package dev.anilbeesetti.nextplayer.feature.videopicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.anilbeesetti.nextplayer.core.ui.api.AndroidPermissionState
import dev.anilbeesetti.nextplayer.feature.videopicker.screens.media.MediaPickerRoute

const val mediaPickerNavigationRoute = "media_picker_screen"

fun NavController.navigateToMediaPickerScreen(navOptions: NavOptions? = null) {
    this.navigate(mediaPickerNavigationRoute, navOptions)
}

fun NavGraphBuilder.mediaPickerScreen(
    permissionState: AndroidPermissionState,
    onSettingsClick: () -> Unit,
    onPlayVideo: (uri: Uri) -> Unit,
    onFolderClick: (path: String) -> Unit
) {
    composable(route = mediaPickerNavigationRoute) {
        MediaPickerRoute(
            permissionState = permissionState,
            onSettingsClick = onSettingsClick,
            onPlayVideo = onPlayVideo,
            onFolderClick = onFolderClick
        )
    }
}
