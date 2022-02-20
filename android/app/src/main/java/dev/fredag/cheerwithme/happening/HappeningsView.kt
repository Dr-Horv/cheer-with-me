package dev.fredag.cheerwithme.happening

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.fredag.cheerwithme.ButtonIcon
import dev.fredag.cheerwithme.FetchStatus
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.*
import dev.fredag.cheerwithme.friends.ErrorSnackbar
import dev.fredag.cheerwithme.friends.UserIcon
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun Happenings(
    happeningsViewModel: HappeningsViewModel,
    openAddHappeningScreen: () -> Unit,
    openHappningScreen: (happening: Happening) -> Unit
) {
    LaunchedEffect(Unit) {
        happeningsViewModel.sendAction(HappeningsViewActions.RefreshHappnings)
    }

    val happenings by happeningsViewModel.viewState.collectAsState()
    val fetchStatus = happenings.fetchStatus
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = openAddHappeningScreen,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }
    }) {
        Surface(modifier = Modifier.padding(20.dp, 16.dp)) {
            Column() {
                ScreenHeaderText("Events")

                SwipeRefresh(
                    state = rememberSwipeRefreshState(fetchStatus == FetchStatus.Loading),
                    onRefresh = { happeningsViewModel.sendAction(HappeningsViewActions.RefreshHappnings) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        happenings.happenings.map {
                            item {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    HappeningRow(
                                        happening = it,
                                        onClick = { openHappningScreen(it) })
                                }
                            }
                        }
                    }
                }
                ErrorSnackbar(
                    visible = happenings.showError,
                    message = if (fetchStatus is FetchStatus.Error) fetchStatus.message else "",
                    onDismiss = { happeningsViewModel.sendAction(HappeningsViewActions.ClearErrorMessage) }


                )
            }
        }

    }
}


@Composable
private fun HappeningRow(
    @PreviewParameter(SampleHappeningProvider::class) happening: Happening,
    onClick: (happening: Happening) -> Unit
) {
    var now by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(0) { // 3
        while (true) {
            now = Instant.now()
            delay(1000)
        }
    }

    return Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(happening) }
    ) {
        Row {
            Box(modifier = Modifier.padding(10.dp)) {
                UserIcon(happening.admin, 42.dp, Modifier)
            }
            Column(
                modifier = Modifier
            ) {
                Text(text = happening.name, fontSize = 20.sp)
                Text(
                    text = durationToEventText(now, happening.time),
                    modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 5.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Row(horizontalArrangement = Arrangement.End) {
                    val overlap = -6
                    val attendeesToShow = 9
                    val attendeesImageSize = 24
                    for ((i, user) in happening.attendees.take(attendeesToShow).withIndex()) {
                        UserIcon(
                            user,
                            attendeesImageSize.dp,
                            modifier = Modifier.offset((i * overlap).dp)
                        )
                    }
                    if (happening.attendees.size > attendeesToShow) {
                        Text(
                            "+${happening.attendees.size - attendeesToShow}",
                            modifier = Modifier.offset(
                                // Offset text to right after the last shown attendee
                                ((attendeesToShow + 1) * overlap + attendeesImageSize + overlap).dp
                            )
                        )
                    }
                }
            }
        }

        // Duplicate on click call hera and on whole row
        // to have ripple effect on row on just on the button
        ButtonIcon(R.drawable.ic_chevron_right_white, onClick = { onClick(happening) })

    }
}


class SampleHappeningProvider : PreviewParameterProvider<Happening> {
    val u1 = User(1L, "Tejp", "https://i.pravatar.cc/300")
    override val values = sequenceOf(
        Happening(
            "1",
            u1,
            "Drinks at the usual place",
            "Bring your friends!",
            Instant.now().plusMillis(1000 * 60 * 60 * 24 * 3),
            Location(Coordinate(11.2, 56.7)),
            listOf(u1), emptyList(), false
        )
    )
}
