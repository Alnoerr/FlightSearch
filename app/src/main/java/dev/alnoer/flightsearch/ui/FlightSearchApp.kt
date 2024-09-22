package dev.alnoer.flightsearch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.alnoer.flightsearch.R
import dev.alnoer.flightsearch.data.Airport
import dev.alnoer.flightsearch.data.Flight

enum class FlightSearchApp {
    HomeScreen,
    DetailsScreen
}

@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FlightSearchApp.HomeScreen.name,
        ) {
            composable(FlightSearchApp.HomeScreen.name) {
                val uiState = viewModel.uiState.collectAsState().value

                val allFlightsList = viewModel.allFlightsList.collectAsState().value
                val suggestionsList =
                    viewModel.getSuggestions(uiState.textFieldValue.text).collectAsState(
                        emptyList()
                    ).value
                val favoritesList = viewModel.favoritesList.collectAsState().value

                HomeScreen(
                    onValueChange = viewModel::setSearchText,
                    onSearch = viewModel::search,
                    onSuggestionClick = viewModel::autocomplete,
                    onFlightClick = {  },
                    onAddFavoriteClick = viewModel::addFavorite,
                    onRemoveFavoriteClick = viewModel::removeFavorite,
                    uiState = uiState,
                    allFlightsList = allFlightsList,
                    suggestionsList = suggestionsList,
                    favoritesList = favoritesList,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
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
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    onSuggestionClick: (Airport) -> Unit,
    onFlightClick: (Flight) -> Unit,
    onAddFavoriteClick: (Flight) -> Unit,
    onRemoveFavoriteClick: (Flight) -> Unit,
    uiState: FlightSearchUiState,
    allFlightsList: List<Airport>,
    suggestionsList: List<Airport>,
    favoritesList: List<Flight>,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

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
                onSearch = {
                    focusManager.clearFocus()
                    onSearch()
                }
            ),
            placeholder = { Text(stringResource(R.string.enter_departure_airport)) },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        AnimatedVisibility(uiState.textFieldValue.text.isEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp)
            ) {
                items(favoritesList) {
                    FlightCard(
                        flight = it,
                        onClick = {},
                        onFavoriteClick = onRemoveFavoriteClick,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
        AnimatedVisibility(uiState.isShowingSuggestions && uiState.textFieldValue.text.isNotEmpty()) {
            LazyColumn {
                items(
                    items = suggestionsList,
                    key = { it.id }
                ) {
                    Suggestion(
                        airport = it,
                        onClick = {
                            focusManager.clearFocus()
                            onSuggestionClick(it)
                        }
                    )
                }
            }
        }
        AnimatedVisibility(!uiState.isShowingSuggestions) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp)
            ) {
                if (uiState.currentAirport != null) {
                    items(
                        items = allFlightsList,
                        key = { it.id }
                    ) {
                        if (uiState.currentAirport != it) {
                            val flight = Flight(
                                departureAirport = uiState.currentAirport,
                                destinationAirport = it,
                                isFavorite = favoritesList.contains(
                                    Flight(
                                        departureAirport = uiState.currentAirport,
                                        destinationAirport = it,
                                        isFavorite = true
                                    )
                                )
                            )
                            FlightCard(
                                flight = flight,
                                onClick = { onFlightClick(flight) },
                                onFavoriteClick = {
                                    if (flight.isFavorite) {
                                        onRemoveFavoriteClick(flight)
                                    } else {
                                        onAddFavoriteClick(flight)
                                    }
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Suggestion(
    airport: Airport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
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

@Composable
fun FlightCard(
    flight: Flight,
    onClick: () -> Unit,
    onFavoriteClick: (Flight) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.weight(1.0f)) {
                FlightPoint(
                    isDepart = true,
                    airport = flight.departureAirport
                )
                FlightPoint(
                    isDepart = false,
                    airport = flight.destinationAirport
                )
            }
            IconButton(
                onClick = { onFavoriteClick(flight) },
                modifier = Modifier
                    .align(
                        alignment = Alignment.CenterVertically
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.favorite),
                    tint = if (flight.isFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun FlightPoint(
    isDepart: Boolean,
    airport: Airport,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (isDepart) stringResource(R.string.depart) else stringResource(R.string.arrive),
            color = Color.Gray
        )
        Row {
            Text(
                text = airport.iataCode,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = airport.name,
                color = Color.Gray
            )
        }
    }
}

//@Preview
//@Composable
//private fun FlightPreview() {
//    FlightSearchTheme {
//        FlightCard(
//            from = Airport(
//                id = 1,
//                name = "Name1",
//                iataCode = "SVO1",
//                passengers = 122
//            ),
//            to = Airport(
//                id = 1,
//                name = "Name2",
//                iataCode = "SVO2",
//                passengers = 122
//            ),
//            onClick = {},
//            isFavorite = true
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//private fun HomeScreenPreview() {
//    FlightSearchTheme {
//        HomeScreen(,
//            onValueChange = {},
//            onSearch = {},
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}