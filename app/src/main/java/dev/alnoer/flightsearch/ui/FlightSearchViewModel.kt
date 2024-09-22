package dev.alnoer.flightsearch.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.alnoer.flightsearch.FlightSearchApplication
import dev.alnoer.flightsearch.model.Airport
import dev.alnoer.flightsearch.model.Flight
import dev.alnoer.flightsearch.data.repository.FlightSearchRepository
import dev.alnoer.flightsearch.data.repository.UserPreferencesRepository
import dev.alnoer.flightsearch.model.toFavorite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlightSearchUiState(
    val textFieldValue: TextFieldValue = TextFieldValue(),
    val isShowingSuggestions: Boolean = false,
    val currentAirport: Airport? = null
)

class FlightSearchViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlightSearchUiState())
    val uiState = _uiState.asStateFlow()

    val favoritesList: StateFlow<List<Flight>> =
        flightSearchRepository.getFavoritesStream().map { favoritesList ->
            favoritesList.map { favorite ->
                Flight(
                    departureAirport = flightSearchRepository.getAirportFromIataCode(favorite.departureCode).first(),
                    destinationAirport = flightSearchRepository.getAirportFromIataCode(favorite.destinationCode).first(),
                    isFavorite = true
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            _uiState.update {
                val searchQuery = userPreferencesRepository.searchQuery.first()
                it.copy(
                    textFieldValue = TextFieldValue(
                        text = searchQuery,
                        selection = TextRange(searchQuery.length)
                    ),
                    currentAirport = flightSearchRepository.getAirportFromIataCode(searchQuery).first(),
                )
            }
        }
    }

    val allFlightsList: StateFlow<List<Airport>> = flightSearchRepository.getAllFlightsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun setSearchText(textFieldValue: TextFieldValue) {
        _uiState.update {
            it.copy(
                isShowingSuggestions = true,
                textFieldValue = textFieldValue
            )
        }
        if (textFieldValue.text.isEmpty()) {
            viewModelScope.launch {
                userPreferencesRepository.saveSearchQuery("")
            }
        }
    }

    fun autocomplete(airport: Airport) {
        _uiState.update {
            it.copy(
                isShowingSuggestions = false,
                textFieldValue = TextFieldValue(
                    text = airport.iataCode,
                    selection = TextRange(airport.iataCode.length)
                ),
                currentAirport = airport
            )
        }
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(airport.iataCode)
        }
    }

    fun getSuggestions(searchQuery: String) =
        flightSearchRepository.getFlightSuggestionsStream(searchQuery)

    fun addFavorite(flight: Flight) {
        viewModelScope.launch {
            flightSearchRepository.addFavorite(flight.toFavorite())
        }
    }

    fun removeFavorite(flight: Flight) {
        viewModelScope.launch {
            flightSearchRepository.removeFavorite(
                flight.departureAirport.iataCode,
                flight.destinationAirport.iataCode
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer{
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(
                    flightSearchRepository = application.container.flightSearchRepository,
                    userPreferencesRepository = application.userPreferencesRepository
                )
            }
        }
    }
}