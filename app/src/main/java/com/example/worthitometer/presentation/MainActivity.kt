/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.worthitometer.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ChipColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
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
import androidx.wear.compose.material.ChipDefaults
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
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ItemViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            LaunchedEffect(shouldUpdateItems) {
                if (shouldUpdateItems) {
                    updatePerDayValue(viewModel)
                }
            }

            WearApp(viewModel)
        }
    }

    private var shouldUpdateItems by mutableStateOf(false)
    override fun onResume() {
        super.onResume()

        shouldUpdateItems = true
    }

    override fun onPause() {
        super.onPause()

        shouldUpdateItems = false
    }
}

fun updatePerDayValue(viewModel: ItemViewModel) {
    viewModel.updateItems {item, index ->
        item.toBuilder()
            .setPerDayValue(calculatePerDayValue(
                LocalDate.parse(item.boughtDate),
                item.productPrice)
            )
            .build()
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearApp(viewModel: ItemViewModel) {
    WorthItOMeterTheme {
        // This adds a structured layer to arrange screen with top level components like time,
        // scroll positions and page indicator.
        AppScaffold{
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
    val listState = rememberResponsiveColumnState()

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
                    ResponsiveListHeader(contentPadding = firstItemPadding()) {
                        Text(text = "Product")
                    }
                }
                item {
                    TextField(
                        label = { Text(
                            "Name",
                            style = TextStyle(
                                fontSize = if (productName.isNotEmpty()) 10.sp else 14.sp,
                                color = Color(red = 50, green = 47, blue = 53)
                            ),
                        )
                        },
                        value = productName,
                        onValueChange = { productName = it },
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(50)),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        shape = RoundedCornerShape(50),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(red = 50, green = 47, blue = 53),
                            unfocusedTextColor =  Color(red = 50, green = 47, blue = 53),
                            unfocusedContainerColor = MaterialTheme.colors.primary,
                            focusedContainerColor = MaterialTheme.colors.primary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Product",
                                tint = Color(red = 50, green = 47, blue = 53)
                            )
                        }
                    )
                }
                item {
                    Log.d("TextFieldPrice", "productValue: $productValue")
                    TextField(
                        label = { Text(
                            "Price",
                            style = TextStyle(
                                    fontSize = if (productValue.isNotEmpty()) 10.sp else 14.sp,
                                    color = Color(red = 50, green = 47, blue = 53)
                                ),
                            )
                        },
                        value = productValue,
                        onValueChange = { productValue = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(50))
                            .padding(top = 0.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        shape = RoundedCornerShape(50),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(red = 50, green = 47, blue = 53),
                            unfocusedTextColor =  Color(red = 50, green = 47, blue = 53),
                            unfocusedContainerColor = MaterialTheme.colors.primary,
                            focusedContainerColor = MaterialTheme.colors.primary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Paid,
                                contentDescription = "Price",
                                tint = Color(red = 50, green = 47, blue = 53)
                            )
                        }
                    )

                }
                item {
                    Chip(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isDatePickerVisible = true },
                        label = { Text("$selectedDate") },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Delete"
                            )
                        }
                    )
                }
                item {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                if (id != null) {
                                    viewModel.deleteItem(id!!.toInt())
                                }

                                navController.navigate("list")
                            },
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                        Spacer(Modifier.size(6.dp))

                        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val formattedDate = selectedDate.format(dateFormatter)
                        Button(
                            onClick = {
                                if (id != null) {
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
                                } else if (productName.isNotEmpty() && productValue.isNotEmpty()){
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
    val daysSinceBought = ChronoUnit.DAYS.between(date, LocalDate.now()) + 1

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
            first = ItemType.Chip
        ),
    )
    val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

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
            itemsIndexed(items) { index, item ->
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("create_or_update?id=${index}")
                    },
                    label = {
                        Text(
                            text = item.product,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "${formatDaysSinceBought(item.boughtDate)} | ${formatter.format(item.perDayValue)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    navController.navigate("create_or_update")
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add New"
                    )
                }
            }
        }
    }
}

fun formatDaysSinceBought(boughtDate: String): String {
   val period = Period.between(LocalDate.parse(boughtDate), LocalDate.now())

    return "${period.years}y ${period.months}m ${if (period.days == 0) 1 else period.days }d"
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {

}