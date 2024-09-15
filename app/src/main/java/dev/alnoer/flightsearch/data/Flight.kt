package dev.alnoer.flightsearch.data

data class Flight(
    val departureAirport: Airport,
    val destinationAirport: Airport,
    val isFavorite: Boolean
)

fun Flight.toFavorite(): Favorite =
    Favorite(
        departureCode = this.departureAirport.iataCode,
        destinationCode = this.destinationAirport.iataCode
    )