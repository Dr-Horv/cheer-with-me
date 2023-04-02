package dev.fredag.cheerwithme.happening

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.search.result.SearchResultType
import com.mapbox.search.result.SearchSuggestion
import com.mapbox.search.result.SearchSuggestionType
import dev.fredag.cheerwithme.AutoCompleteTextView
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.Coordinate
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// TODO started fixing getting location permisisons
// Copying some code from here
// https://github.com/kahdichienja/jetMap/blob/main/app/src/main/java/com/kchienja/jetmap/MainActivity.kt
// https://developer.android.com/training/location/retrieve-current
//private fun getLocationPermission(context: Context){
//    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//        //TODO
//        viewModel.permissionGrand(true)
//        getDeviceLocation()
//    } else {
//        Log.d("Exception", "Permission not granted")
//    }
//}
//
//private  fun getDeviceLocation(){
//    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//    try {
//        if (viewModel.locationPermissionGranted.value ==true){
//            val locationResult = fusedLocationProviderClient.lastLocation
//
//            locationResult.addOnCompleteListener {
//                    task ->
//                if (task.isSuccessful){
//                    val lastKnownLocation = task.result
//
//                    if (lastKnownLocation != null){
//                        viewModel.currentUserGeoCOord(
//                            LatLng(
//                                lastKnownLocation.altitude,
//                                lastKnownLocation.longitude
//                            )
//                        )
//                    }
//                }else{
//                    Log.d("Exception"," Current User location is null")
//                }
//            }
//
//        }
//
//    }catch (e: SecurityException){
//        Log.d("Exception", "Exception:  $e.message.toString()")
//    }
//}

@Composable
fun NewHappeningScreen(
    happeningViewModel: NewHappeningViewModel,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val viewState by happeningViewModel.viewState.collectAsState()
    val viewEvents = happeningViewModel.viewEvent


    Column(modifier = Modifier.padding(20.dp, 16.dp)) {
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScreenHeaderText(text = "New Event")
            Button(onClick = {
                happeningViewModel.sendAction(
                    NewHappeningViewAction.SubmitHappening()
                )
            }) {
                Text(text = "Create", color = Color.White)
            }
        }
        OutlinedTextField(
            value = viewState.name,
            onValueChange = { happeningViewModel.sendAction(NewHappeningViewAction.SetName(it)) },
            label = { Text(text = "Event Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewState.description,
            onValueChange = { happeningViewModel.sendAction(NewHappeningViewAction.SetDescription(it)) },
            label = { Text(text = "Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "When", modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp))
            Row(modifier = Modifier.padding(0.dp, 10.dp)) {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                happeningViewModel.sendAction(
                                    NewHappeningViewAction.SetDate(
                                        year,
                                        month,
                                        dayOfMonth
                                    )
                                )
//            selectedDate = calendar.timeInMillis
                            },
                            viewState.date.year,
                            viewState.date.monthValue - 1,
                            viewState.date.dayOfMonth
                        ).show()

                    }) {
                    Icon(
                        Icons.Filled.Event,
                        contentDescription = null
                    )
                    Text(text = formatDate(LocalDate.now(), viewState.date), color = Color.White)
                }
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                happeningViewModel.sendAction(
                                    NewHappeningViewAction.SetTime(
                                        hour,
                                        minute,
                                    )
                                )
//            selectedDate = calendar.timeInMillis
                            },
                            viewState.time.hour,
                            viewState.time.minute,
                            true,
                        ).show()

                    }) {
                    Icon(Icons.Filled.WatchLater, contentDescription = null)
                    Text(text = formatTime(viewState.time), color = Color.White)
                }


            }

        }

        //Row(verticalAlignment = Alignment.CenterVertically) {
//            Text(text = "Where", modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp))
//            Checkbox(checked = viewState.shouldHavelocation, onCheckedChange = {
//                happeningViewModel.sendAction(NewHappeningViewAction.ToggleShouldHaveLocation)
//            })
        //if (viewState.loadingLocationSearch) CircularProgressIndicator()
        //}
        AutoCompleteTextView(
            modifier = Modifier.fillMaxWidth(),
            query = viewState.locationSearchText,
            queryLabel = "Location",
            onQueryChanged = { updatedAddress ->
                happeningViewModel.sendAction(
                    NewHappeningViewAction.UpdateLocationSearchQuery(
                        updatedAddress
                    )
                )
            },
            predictions = viewState.locationSearchSuggestions,
            onClearClick = {
                happeningViewModel.sendAction(NewHappeningViewAction.ClearLocationSearch)
            },
            //onDoneActionClick = { newLocationUIAction(NewLocationUIAction.OnLocationAutoCompleteDone) },
            onItemClick = { placeItem ->
                happeningViewModel.sendAction(
                    NewHappeningViewAction.SelectLocationSearchSuggestion(
                        placeItem
                    )
                )
            },
            hidePredictions = viewState.hideLocationSearchSuggestions
        ) {
            Text(text = formatSearchSuggestion(it))
        }
        val markers =
            viewState.location?.let { Point.fromLngLat(it.coordinate.lng, it.coordinate.lat) }
                ?.let { listOf(it) } ?: emptyList()
        Log.d("HELO", "markers $markers")

        Mapview(markers = markers, viewEvents = viewEvents) {
            happeningViewModel.sendAction(
                NewHappeningViewAction.PlaceMarker(
                    Coordinate(
                        it.latitude(),
                        it.longitude()
                    )
                )
            )
        }

    }


}

fun makeMapAnnotation(point: Point, markerImage: Bitmap): PointAnnotationOptions {
    val annotationOptions = PointAnnotationOptions()
    annotationOptions
        .withPoint(point)
        .withIconImage(markerImage)
        .withIconOffset(listOf(0.0, -7.5))
        .withIconSize(1.5)
        //.withTextField("Happityhaps")
        .withTextHaloBlur(2.0)
        .withTextHaloWidth(1.0)
        .withTextHaloColor(android.graphics.Color.rgb(0x82, 0x59, 0x00))
        .withTextLetterSpacing(0.1)
        .withTextColor(
            android.graphics.Color.rgb(0xf9, 0xab, 0x02)
        )
        .withTextOffset(listOf(0.0, -2.5))
    return annotationOptions
}

@Composable
fun Mapview(
    modifier: Modifier = Modifier,
    markers: List<Point> = emptyList(),
    viewEvents: SharedFlow<NewHappeningViewEvent>,
    markerPlaced: (Point) -> Unit = {},
) {
    val mapMarkerImage = ImageBitmap.imageResource(R.drawable.ic_map_marker).asAndroidBitmap()

    var pressedLocation by remember {
        mutableStateOf<Point?>(null)
    }
    var mapRef by remember {
        mutableStateOf<MapboxMap?>(null)
    }
    var annotationsManagerRef by remember {
        mutableStateOf<PointAnnotationManager?>(null)
    }
    var annotationsRef by remember {
        mutableStateOf<AnnotationPlugin?>(null)
    }

    LaunchedEffect(Unit) {
        viewEvents.collect {
            if (it is NewHappeningViewEvent.FlyTo) {
                mapRef?.flyTo(
                    cameraOptions {
                        center(Point.fromLngLat(it.coordinate.lng, it.coordinate.lat))
                        zoom(12.0)
                    }
                )
            }
        }
    }

    LaunchedEffect(mapRef, pressedLocation, markers) {
        annotationsManagerRef?.let { annotationsRef?.removeAnnotationManager(it) }
        annotationsManagerRef = annotationsRef?.createPointAnnotationManager(AnnotationConfig())

        for (marker in markers) {
            annotationsManagerRef?.create(makeMapAnnotation(marker, mapMarkerImage))
        }

        pressedLocation?.let {
            annotationsManagerRef?.create(makeMapAnnotation(it, mapMarkerImage))

        }

    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                val map = getMapboxMap()
                map.loadStyleUri(Style.DARK)
                mapRef = map
                annotationsRef = annotations

                map.addOnMapClickListener {
                    pressedLocation = it
                    markerPlaced(it)
                    false
                }
            }
        }
    )
}

fun formatDate(currentDate: LocalDate, date: LocalDate): String {
    val builder = StringBuilder()
    builder.append(date.dayOfMonth)
    builder.append("-")

    builder.append(
        date.month.getDisplayName(
            TextStyle.SHORT,
            Resources.getSystem().configuration.locales.get(0)
        )
    )

    if (currentDate.year != date.year) {
        builder.append(" ")
        builder.append(date.year)
    }

    return builder.toString()
}

fun formatTime(time: LocalTime): String {
    val dtf = DateTimeFormatter.ofPattern("HH:mm")
    return dtf.format(time)
}