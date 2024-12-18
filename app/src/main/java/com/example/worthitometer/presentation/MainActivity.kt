/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.worthitometer.presentation

import ItemRepository
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.navigation.NavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.worthitometer.R
import com.example.worthitometer.presentation.theme.WorthItOMeterTheme
import com.example.worthitometer.viewmodel.ItemViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.worthItOMeter.Item
import itemListDataStore
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
            val context = LocalContext.current
            val repository = ItemRepository(context.itemListDataStore)
            val viewModel = ItemViewModel(repository)

            val navController = rememberSwipeDismissableNavController()
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "list"
            ) {
                composable("list") {
                    ListScreen(viewModel, navController)
                }
                composable("create_or_update?id={id}") {
                    CreateScreen(id = it.arguments?.getString("id"), viewModel, navController)
                }
            }
        }
    }
}
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun CreateScreen(id: String?, viewModel: ItemViewModel, navController: NavController) {

    // This helps with the resizing on ScalingLazyColumn by informing the first and last
    // type of the items on the column
    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        ),
    )

    val items by viewModel.items.collectAsState()


    var productName by remember { mutableStateOf("") }
    var productValue by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isDatePickerVisible by remember { mutableStateOf(false) }


    if (id != null) {
        val itemFlow = viewModel.getItem(id.toInt())
        val item by itemFlow.collectAsState(initial = null)
        if (item != null) {
            productName = item?.product.toString()
            productValue = item?.productPrice.toString()
            selectedDate = LocalDate.parse(item?.boughtDate)
        }
    }

    // I think this is what actually adds the scroll position on the screen
    ScreenScaffold(
        scrollState = listState
    ) {
        if (!isDatePickerVisible) {
            // This properly resize and add layout modifications to components when scrolling for
            // rounded screens
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                columnState = listState,
            ) {
                item {
                    TextField(
                        value = productName,
                        onValueChange = { productName = it },
                        placeholder = { Text("Produce Name") },
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(50)),
                        textStyle = TextStyle(fontSize = 14.sp),
                        shape = RoundedCornerShape(50)
                    )
                }
                item {
                    TextField(
                        value = productValue,
                        onValueChange = { productValue = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = { Text("0,00") },
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(50)),
                        textStyle = TextStyle(fontSize = 14.sp),
                        shape = RoundedCornerShape(50)
                    )

                }
                item {
                    Chip(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isDatePickerVisible = true },
                        label = { Text("$selectedDate") }
                    )
                }
                item {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { "does nothing for now" },
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel"
                            )
                        }
                        Spacer(Modifier.size(6.dp))

                        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val formattedDate = selectedDate.format(dateFormatter)
                        Button(
                            onClick = {
                                if (id != null) {
                                    Log.d("CreateScreenButton", "updating item $id in data_store")

                                    val updatedItem = (
                                        Item.newBuilder()
                                            .setProduct(productName)
                                            .setProductPrice(productValue.toFloat())
                                            .setBoughtDate(formattedDate)
                                            .setPerDayValue(
                                                calculatePerDayValue(selectedDate, productValue.toFloat())
                                            )
                                            .build()
                                    )
                                    viewModel.editItem(id.toInt(), updatedItem)
                                    Log.d("CreateScreenButton", "updated item $id in data_store complete")

                                } else {

                                Log.d("CreateScreenButton", "inserting in data_store")
                                viewModel.addItem(
                                    productName,
                                    productValue.toFloat(),
                                    formattedDate,
                                    calculatePerDayValue(selectedDate, productValue.toFloat())
                                )
                                Log.d("CreateScreenButton", "Insertion in data_store finished")

                                }

                                navController.navigate("list")
                            },
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Create"
                            )
                        }
                    }
                }
            }
        } else {
            DatePicker(
                date = selectedDate,
                onDateConfirm = { date ->
                    selectedDate = date
                    println("Selected date updated: $selectedDate")
                    isDatePickerVisible = false
                },
                toDate = LocalDate.now()
            )
        }
    }
}

fun calculatePerDayValue(date: LocalDate, productValue: Float): Float {
    val daysSinceBought = ChronoUnit.DAYS.between(date, LocalDate.now())
    Log.d("calculatePerDayValue", "since bought: $daysSinceBought")

    val valuePerDay = productValue / daysSinceBought

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    val roundedValue = df.format(valuePerDay)

    Log.d("calculatePerDayValue", "value per day: $valuePerDay")

    return roundedValue.toFloat()
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ListScreen(viewModel: ItemViewModel, navController: NavController) {

    val items by viewModel.items.collectAsState()
    val itemCount = items.size

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
                Button(onClick = {
                    navController.navigate("create_or_update")
                }) {
                    Text("Add Item")
                }
            }
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = "Products")
                }
            }
            itemsIndexed(items) { index, item ->
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("create_or_update?id=${index}")
                    },
                    label = {
                        Text(
                            text = "${index + 1}. ${item.product}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "R$ ${item.perDayValue}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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