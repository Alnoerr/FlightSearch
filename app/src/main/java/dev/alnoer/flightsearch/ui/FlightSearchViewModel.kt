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
import dev.alnoer.flightsearch.FlightSearchApplication
import dev.alnoer.flightsearch.data.Airport
import dev.alnoer.flightsearch.data.Favorite
import dev.alnoer.flightsearch.data.FlightSearchRepository
import dev.alnoer.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlightSearchUiState(
    val textFieldValue: TextFieldValue = TextFieldValue(),
    val isShowingSuggestions: Boolean = false,
    val suggestionsList: List<Airport> = emptyList(),
    val flightsList: List<Airport> = emptyList(),
    val favoritesList: List<Favorite> = emptyList()
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
                    )
                )
            }
        }
    }

    fun setSearchText(textFieldValue: TextFieldValue) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isShowingSuggestions = true,
                    textFieldValue = textFieldValue,
                    suggestionsList = flightSearchRepository.getFlightSuggestionsStream(
                        textFieldValue.text
                    ).first()
                )
            }
        }
    }

    fun search() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isShowingSuggestions = false,
                    flightsList = flightSearchRepository.getFlightsStream(
                        uiState.value.textFieldValue.text
                    ).first()
                )
            }
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