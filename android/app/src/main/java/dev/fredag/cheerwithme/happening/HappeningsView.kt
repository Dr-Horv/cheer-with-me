package dev.fredag.cheerwithme.happening

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.fredag.cheerwithme.data.backend.Coordinate
import dev.fredag.cheerwithme.data.backend.Happening
import dev.fredag.cheerwithme.data.backend.Location
import dev.fredag.cheerwithme.data.backend.User
import java.time.Duration
import java.time.Instant

@Composable
fun Happenings(happeningsViewModel: HappeningsViewModel, openAddHappeningScreen: () -> Unit) {
    val happenings = happeningsViewModel.happenings.observeAsState()
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = openAddHappeningScreen,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }
    }) {
        Surface {
            Column(modifier = Modifier.padding(20.dp, 16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,

                    ) {
                    happenings.value?.map {
                        HappeningRow(it)
                    }
                }
            }
        }

    }
}

@Preview()
@Composable
private fun HappeningRow(@PreviewParameter(SampleHappeningProvider::class) happening: Happening) {
    val timeUntil = Duration.between(happening.time, Instant.now())
    return Row() {
        Text(text = happening.name)
        Text(text = "in")
        Text(text = sensibleDurationString(timeUntil))
    }
}

fun sensibleDurationString(d: Duration): String {
    return if (d.toDays() > 0) {
        "${d.toDays()} days"
    } else if (d.toHours() > 0) {
        "${d.toHours()}"
    } else if (d.toMinutes() > 0) {
        "${d.toMinutes()}"
    } else {
        "Very Soon"
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