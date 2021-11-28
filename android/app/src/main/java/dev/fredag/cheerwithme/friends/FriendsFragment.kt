package dev.fredag.cheerwithme.friends

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.data.backend.UserId
import dev.fredag.cheerwithme.ui.CheerWithMeTheme
import dev.fredag.cheerwithme.ui.beerYellow
import kotlinx.coroutines.FlowPreview
import java.util.*


@OptIn(FlowPreview::class)
@Composable
fun Friends(friendsViewModel: FriendsViewModel) = CheerWithMeTheme {
    Surface {
        Column(modifier = Modifier.padding(20.dp, 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,

                ) {
                val friends = friendsViewModel.friends.observeAsState()
                Log.d("Friends", friends.value.toString())
                if (friends.value?.incomingFriendRequests?.isNotEmpty() == true) {
                    FriendRequestsList(
                        friends.value?.incomingFriendRequests ?: emptyList(),
                        onAcceptFriendRequest = friendsViewModel::acceptFriendRequest
                    )
                }
                FriendsList(friends.value?.friends ?: emptyList())
            }
        }
    }
}


@Composable
fun FriendsList(friends: List<User> = listOf()) {
    Text(text = "Friends".toUpperCase(Locale.getDefault()))
    friends.sortedBy { it.nick }.forEach {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            UserWithIcon(it)
        }
    }
}

@Composable
private fun FriendRequestsList(
    requests: List<User> = listOf(),
    onAcceptFriendRequest: (UserId) -> Unit = {},
    onDenyFriendRequest: (UserId) -> Unit = {},
) {
    Text(text = "Friend Requests".toUpperCase(Locale.getDefault()))
    requests.forEach {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ) {
            UserWithIcon(it)
            Row {
                IconButton(onClick = {
                    Log.d(
                        "Friends",
                        "Deny friend request clicked"
                    )
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_times_circle),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                        modifier = Modifier.size(40.dp),
                        contentDescription = ""

                    )
                }
                IconButton(onClick = {
                    Log.d(
                        "Friends",
                        "Accept friend request clicked for ${it.id}:${it.nick}"
                    )
                    onAcceptFriendRequest(it.id)
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_check_circle),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                        modifier = Modifier.size(40.dp),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
private fun UserWithIcon(
    it: User,
) {
    val img = if (it.avatarUrl !== null && false) {
        TODO("Fetch image from internet for avatar here")
    } else {
        painterResource(id = R.drawable.ic_profile_black_24dp)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = img,
            modifier = Modifier
                .size(32.dp)
                .background(beerYellow, CircleShape)
                .clip(CircleShape),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = ""
        )
        Text(
            it.nick,
            modifier = Modifier.padding(8.dp, 0.dp)
        )
    }
}