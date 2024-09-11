package dev.alnoer.flightsearch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.alnoer.flightsearch.R
import dev.alnoer.flightsearch.data.Airport
import dev.alnoer.flightsearch.ui.theme.FlightSearchTheme

enum class FlightSearchApp {
    HomeScreen,
    DetailsScreen
}

@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val navController = rememberNavController()
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FlightSearchApp.HomeScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(FlightSearchApp.HomeScreen.name) {
                HomeScreen(
                    uiState = uiState,
                    onValueChange = viewModel::setSearchText,
                    onSearch = viewModel::search,
                    modifier = Modifier.fillMaxSize()
                )
            }

            val flightSearchArgument = "flightRoute"
            composable(
                route = FlightSearchApp.DetailsScreen.name + "/{$flightSearchArgument}",
                arguments = listOf(navArgument(flightSearchArgument) { type = NavType.StringType })
            ) {
                //TODO
            }
        }
    }
}

@Composable
fun HomeScreen(
    uiState: FlightSearchUiState,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = uiState.textFieldValue,
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            ),
            placeholder = { Text(stringResource(R.string.enter_departure_airport)) },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        AnimatedVisibility(uiState.textFieldValue.text.isEmpty()) {
            LazyColumn {
                // Favorites
            }
        }
        AnimatedVisibility(uiState.isShowingSuggestions) {
            LazyColumn {
                items(uiState.suggestionsList) {
                    Suggestion(it)
                }
            }
        }
        AnimatedVisibility(!uiState.isShowingSuggestions) {
            LazyColumn {
                // Results
            }
        }
    }
}

@Composable
fun Suggestion(
    airport: Airport,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Text(
            text = airport.iataCode,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(end = 4.dp))
        Text(
            text = airport.name,
            fontWeight = FontWeight.Light,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FlightSearchTheme {
        HomeScreen(
            uiState = FlightSearchUiState(),
            onValueChange = {},
            onSearch = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}