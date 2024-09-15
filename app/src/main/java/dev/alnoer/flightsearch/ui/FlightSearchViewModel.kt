package dev.alnoer.flightsearch.ui

import android.provider.SearchRecentSuggestions
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Query
import dev.alnoer.flightsearch.FlightSearchApplication
import dev.alnoer.flightsearch.data.Airport
import dev.alnoer.flightsearch.data.Favorite
import dev.alnoer.flightsearch.data.Flight
import dev.alnoer.flightsearch.data.FlightSearchRepository
import dev.alnoer.flightsearch.data.UserPreferencesRepository
import dev.alnoer.flightsearch.data.toFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    init {
        viewModelScope.launch {
            _uiState.update {
                val searchQuery = userPreferencesRepository.searchQuery.first()
                it.copy(
                    textFieldValue = TextFieldValue(
                        text = searchQuery,
                        selection = TextRange(searchQuery.length)
                    ),
                    currentAirport = flightSearchRepository.getAirportFromIataCode(searchQuery).first()
                )
            }
        }
    }

    fun setSearchText(textFieldValue: TextFieldValue) {
        _uiState.update {
            it.copy(
                isShowingSuggestions = true,
                textFieldValue = textFieldValue
            )
        }
    }

    fun search() {
        _uiState.update {
            it.copy(
                isShowingSuggestions = false
            )
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

    fun getFavorites(): Flow<List<Favorite>> = flightSearchRepository.getFavoritesStream()

    fun getSuggestions(searchQuery: String): Flow<List<Airport>> =
        flightSearchRepository.getFlightSuggestionsStream(searchQuery)

    fun getAllFlights(): Flow<List<Airport>> =
        flightSearchRepository.getAllFlightsStream()

    fun getAirportFromIataCode(iataCode: String) =
        flightSearchRepository.getAirportFromIataCode(iataCode)

    fun addFavorite(flight: Flight) {
        viewModelScope.launch {
            flightSearchRepository.addFavorite(flight.toFavorite())
        }
    }

    fun removeFavorite(flight: Flight) {
        viewModelScope.launch {
            flightSearchRepository.removeFavorite(flight.toFavorite())
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