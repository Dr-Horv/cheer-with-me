package dev.fredag.cheerwithme.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Recomposer
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.setContent
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.data.backend.UserId
import dev.fredag.cheerwithme.ui.CheerWithMeTheme
import dev.fredag.cheerwithme.ui.beerYellow
import java.util.*

@AndroidEntryPoint
class FriendsFragment : Fragment() {
    val friendsViewModel: FriendsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView =
            inflater.inflate(R.layout.fragment_friends, container, false) as ViewGroup
        fragmentView.setContent(Recomposer.current()) {
            Friends(this)
        }

        return fragmentView
    }
}

@Composable
fun Friends(fragment: FriendsFragment) = CheerWithMeTheme {
    Surface {
        Column(modifier = Modifier.padding(20.dp, 16.dp)) {
            ScrollableColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalGravity = Alignment.Start
            ) {
                val friends = fragment.friendsViewModel.friends.observeAsState()
                Log.d("Friends", friends.value.toString())
                if (friends.value?.incomingFriendRequests?.isNotEmpty() == true) {
                    FriendRequestsList(
                        friends.value?.incomingFriendRequests ?: emptyList(),
                        onAcceptFriendRequest = fragment.friendsViewModel::acceptFriendRequest
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
            verticalGravity = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(50.dp),
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
            verticalGravity = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(60.dp),
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
                        asset = vectorResource(id = R.drawable.ic_times_circle),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                        modifier = Modifier.size(40.dp)

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
                        asset = vectorResource(id = R.drawable.ic_check_circle),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                        modifier = Modifier.size(40.dp)
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
        vectorResource(id = R.drawable.ic_profile_black_24dp)
    }
    Row(
        verticalGravity = Alignment.CenterVertically,
    ) {
        Image(
            asset = img,
            modifier = Modifier.size(32.dp)
                .drawBackground(beerYellow, CircleShape)
                .clip(CircleShape),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Text(
            it.nick,
            modifier = Modifier.padding(8.dp, 0.dp)
        )
    }
}