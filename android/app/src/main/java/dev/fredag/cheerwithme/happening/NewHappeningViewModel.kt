package dev.fredag.cheerwithme.happening

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.search.*
import com.mapbox.search.result.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.fredag.cheerwithme.BaseViewModel
import dev.fredag.cheerwithme.data.backend.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.time.*
import java.time.DayOfWeek.*
import javax.inject.Inject

data class NewHappeningViewState(
    val showUnfinishedHints: Boolean = false,
    val newHappening: Happening? = null,

    val name: String = "",
    val description: String = "",
    val time2: Instant = nextFridayAtSix(LocalDateTime.now()),
    val date: LocalDate = nextFridayAtSix2(LocalDateTime.now()).toLocalDate(),
    val time: LocalTime = nextFridayAtSix2(LocalDateTime.now()).toLocalTime(),
    val location: Location? = null,
    val shouldHavelocation: Boolean = true,

    val loadingLocationSearch: Boolean = false,
    val locationSearchNearbyNames: List<String>? = null,
    val locationSearchText: String = "",
    val locationSearchSuggestions: List<SearchSuggestion> = emptyList(),
    val hideLocationSearchSuggestions: Boolean = true,
)

sealed class NewHappeningViewAction {
    class SetName(val name: String) : NewHappeningViewAction()
    class SetDescription(val description: String) : NewHappeningViewAction()
    class SetDate(val year: Int, val month: Int, val dayOfMonth: Int) : NewHappeningViewAction()
    class SetTime(val hour: Int, val minute: Int) : NewHappeningViewAction()
    class SearchByCoordinate(val coordinate: Coordinate) : NewHappeningViewAction()
    class SearchByName(val name: String) : NewHappeningViewAction()
    object ToggleShouldHaveLocation : NewHappeningViewAction()
    class PlaceMarker(val coordinate: Coordinate) : NewHappeningViewAction()
    class SubmitHappening : NewHappeningViewAction() {

    }

    class UpdateLocationSearchQuery(val query: String) : NewHappeningViewAction()
    object CloseLocationSearchSuggestions : NewHappeningViewAction()
    object ClearLocationSearch : NewHappeningViewAction()
    class SelectLocationSearchSuggestion(val suggestion: SearchSuggestion) :
        NewHappeningViewAction()
}

sealed class NewHappeningViewEvent {
    class FlyTo(val coordinate: Coordinate): NewHappeningViewEvent()
}

@HiltViewModel
class NewHappeningViewModel @Inject constructor(private val happeningsRepository: HappeningsRepository) :
    BaseViewModel<NewHappeningViewState, NewHappeningViewAction, NewHappeningViewEvent>() {

    private var locationSearchJob: Job? = null
    private val reverseEngine = MapboxSearchSdk.getReverseGeocodingSearchEngine()
    private val searchEngine = MapboxSearchSdk.getSearchEngine()


    val searchSelectionCallback = object : SearchSelectionCallback {
        override fun onCategoryResult(
            suggestion: SearchSuggestion,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            Log.d("HELO", "onCategoryResult $suggestion\n")
            Log.d("HELO", "onCategoryResult $results\n")
            Log.d("HELO", "onCategoryResult $responseInfo\n")
        }

        override fun onError(e: Exception) {
            Log.d("HELO", "onError $e\n")
        }

        override fun onResult(
            suggestion: SearchSuggestion,
            result: SearchResult,
            responseInfo: ResponseInfo
        ) {
            result.coordinate?.let { p ->
                val coordinate = Coordinate(
                    p.latitude(),
                    p.longitude()
                )
                setState {
                    it.copy(
                        location = Location(
                            coordinate
                        )
                    )
                }
                sendEvent(NewHappeningViewEvent.FlyTo(coordinate))
            }
            Log.d("HELO", "onResult $suggestion\n")
            Log.d("HELO", "onResult $result\n")
            Log.d("HELO", "onResult $responseInfo\n")
        }

        override fun onSuggestions(
            suggestions: List<SearchSuggestion>,
            responseInfo: ResponseInfo
        ) {
            setState {
                it.copy(locationSearchSuggestions = suggestions.filter {
                    it.address?.country != null &&
                            it.address?.postcode != null &&
                            it.address?.street != null
                })
            }
        }
    }


    override fun handleAction(action: NewHappeningViewAction) {
        when (action) {
            is NewHappeningViewAction.SetName -> setState { it.copy(name = action.name) }
            is NewHappeningViewAction.SetDescription -> setState { it.copy(description = action.description) }
            is NewHappeningViewAction.SetDate -> setState {
                it.copy(
                    date = LocalDate.of(
                        action.year,
                        action.month + 1,
                        action.dayOfMonth
                    )
                )
            }
            is NewHappeningViewAction.SetTime -> setState {
                it.copy(
                    time = LocalTime.of(
                        action.hour,
                        action.minute
                    )
                )
            }
            is NewHappeningViewAction.SearchByCoordinate -> TODO()
            is NewHappeningViewAction.SearchByName -> TODO()
            is NewHappeningViewAction.SubmitHappening -> {
                viewModelScope.launch {
                   val resp = happeningsRepository.createHappening(
                       CreateHappening(
                           viewState.value.name,
                           viewState.value.description,
                           viewState.value.time2,
                           viewState.value.location,
                           emptyList()
                       )
                   )

                }
            }
            NewHappeningViewAction.ToggleShouldHaveLocation -> setState {
                it.copy(
                    shouldHavelocation = !it.shouldHavelocation,

                    )
            }
            is NewHappeningViewAction.PlaceMarker -> {

                setState {
                    it.copy(
                        loadingLocationSearch = true,
                        location = Location(action.coordinate)
                    )
                }
                viewModelScope.launch {
                    reverseEngine.search(
                        ReverseGeoOptions(
                            center = Point.fromLngLat(action.coordinate.lng, action.coordinate.lat)
                        ),
                        object : SearchCallback {
                            override fun onError(e: Exception) {
                                setState { it.copy(loadingLocationSearch = false) }
                            }

                            override fun onResults(
                                results: List<SearchResult>,
                                responseInfo: ResponseInfo
                            ) {
                                setState {
                                    it.copy(
                                        loadingLocationSearch = false,
                                        locationSearchNearbyNames = results.map { r -> r.address.toString() })
                                }
                            }
                        }
                    )
                }
            }
            is NewHappeningViewAction.UpdateLocationSearchQuery -> {
                setState {
                    it.copy(
                        locationSearchText = action.query,
                        hideLocationSearchSuggestions = false
                    )
                }
                locationSearchJob?.cancel()
                locationSearchJob = viewModelScope.launch {
                    searchEngine.search(
                        action.query,
                        SearchOptions(limit = 5, types = listOf(QueryType.POI, QueryType.ADDRESS)),
                        searchSelectionCallback
                    )
                }
            }
            NewHappeningViewAction.ClearLocationSearch -> setState {
                it.copy(
                    locationSearchText = "",
                    locationSearchSuggestions = emptyList()
                )
            }
            NewHappeningViewAction.CloseLocationSearchSuggestions -> setState {
                it.copy(
                    hideLocationSearchSuggestions = true
                )
            }
            is NewHappeningViewAction.SelectLocationSearchSuggestion -> {
                searchEngine.select(action.suggestion, searchSelectionCallback)
                setState {
                    it.copy(
                        locationSearchText = formatSearchSuggestion(action.suggestion),
                        hideLocationSearchSuggestions = true
                    )
                }
            }
        }
    }

    override fun initialViewState() = NewHappeningViewState()
}


fun formatSearchSuggestion(
    searchSuggestion: SearchSuggestion
): String {
    val type = searchSuggestion.type
    val sb = StringBuilder()
    if (type is SearchSuggestionType.SearchResultSuggestion) {
        if (SearchResultType.POI in type.types || SearchResultType.PLACE in type.types) {
            sb.append("${searchSuggestion.name} - ")
        }
    } else if (type is SearchSuggestionType.IndexableRecordItem) {
        if (type.type == SearchResultType.POI || type.type == SearchResultType.PLACE) {
            sb.append("${searchSuggestion.name} - ")
        }
    }
    sb.append(formatAddress(searchSuggestion.address))
    return sb.toString()
}


fun formatAddress(address: SearchAddress?): String {
    if (address == null) return ""
    val s = StringBuilder()
    address.street?.let {
        s.append(it)
        s.append(" ")
    }
    address.houseNumber?.let {
        s.append(it)
        s.append(" ")
    }
    address.postcode?.let {
        s.append(", ")
        s.append(it)
        s.append(" ")
    }
    address.place?.let {
        s.append(it)
    }
    return s.toString()
//    SearchAddress(
//        houseNumber=53,
//        street=Marklandsgatan,
//        neighborhood=null,
//        locality=null,
//        postcode=414 77,
//        place=Gothenburg,
//        district=null,
//        region=Västra Götaland,
//        country=Sweden)
}

fun nextFridayAtSix(now: LocalDateTime): Instant {
    val date = now.withHour(18).withMinute(0).withSecond(0)

    val daysUntilFriday = when (date.dayOfWeek) {
        MONDAY -> 4
        TUESDAY -> 3
        WEDNESDAY -> 2
        THURSDAY -> 1
        FRIDAY -> 0
        SATURDAY -> 6
        SUNDAY -> 5
    }.toLong()

    return if (daysUntilFriday == 0L && now.hour >= 18) {
        date.plusDays(7).toInstant(ZoneOffset.UTC)
    } else {
        date.plusDays(daysUntilFriday).toInstant(ZoneOffset.UTC)
    }
}

fun nextFridayAtSix2(now: LocalDateTime): LocalDateTime {
    val date = now.withHour(18).withMinute(0).withSecond(0)

    val daysUntilFriday = when (date.dayOfWeek) {
        MONDAY -> 4
        TUESDAY -> 3
        WEDNESDAY -> 2
        THURSDAY -> 1
        FRIDAY -> 0
        SATURDAY -> 6
        SUNDAY -> 5
    }.toLong()

    return if (daysUntilFriday == 0L && now.hour >= 18) {
        date.plusDays(7)
    } else {
        date.plusDays(daysUntilFriday)
    }
}