/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.worthitometer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.worthitometer.R
import com.example.worthitometer.presentation.theme.WorthItOMeterTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearApp(greetingName: String) {
    WorthItOMeterTheme {
        // This adds a structured layer to arrange screen with top level components like time,
        // scroll positions and page indicator.
        AppScaffold{
            ListScreen()
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ListScreen() {
    // This helps with the resizing on ScalingLazyColumn by informing the first and last
    // type of the items on the column
    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    // I think this is what actually adds the scroll position on the screen
    ScreenScaffold(
        scrollState = listState
    ) {
        // This properly resize and add layout modifications to components when scrolling for
        // rounded screens
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            columnState = listState,
        ){
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = "Products")
                }
            }
            items(10) {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    label = { Text(text = "Product Name", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    secondaryLabel = {
                        Text(text = "R$ $it,00", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                )
            }
        }
    }
}
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}