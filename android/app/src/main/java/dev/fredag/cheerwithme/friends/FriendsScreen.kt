package dev.fredag.cheerwithme.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.FlowPreview
import java.util.*

@OptIn(FlowPreview::class)
@Composable
fun Friends(friendsViewModel: FriendsViewModel, openAddFriendScreen: () -> Unit) {
    LaunchedEffect(Unit) {
        friendsViewModel.fetchFriends()
    }
    val _friendsViewState by friendsViewModel.friendsViewState.collectAsState()
    // Redeclare to fix smartcast
    val friendsViewState = _friendsViewState

    val loading = friendsViewState.fetchStatus == FetchStatus.Loading

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = openAddFriendScreen,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        },
    ) {
        Surface(modifier = Modifier.padding(20.dp, 16.dp)) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(loading),
                onRefresh = friendsViewModel::fetchFriends,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {


                    friendsViewState.friendsModel.incomingFriendRequests.takeIf { it.isNotEmpty() }
                        ?.let {
                            item {
                                Text(text = "Friend Requests".toUpperCase(Locale.getDefault()))
                            }
                            it.map {
                                item {
                                    FriendRequestItem(
                                        user = it,
                                        onAcceptFriendRequest = friendsViewModel::acceptFriendRequest,
                                        onDenyFriendRequest = {} // TODO https://github.com/fredagsdeploy/cheer-with-me/issues/31
                                    )
                                }

                            }
                        }

                    friendsViewState.friendsModel.let {
                        item {
                            Text(text = "Friends".uppercase(Locale.getDefault()))
                        }
                        it.friends.map {
                            item {
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

                    }
                }
            }
        }
        ErrorSnackbar(
            friendsViewState.showError,
            if (friendsViewState.fetchStatus is FetchStatus.Error) friendsViewState.fetchStatus.message else "",
            onDismiss = { friendsViewModel.sendAction(FriendsViewActions.ClearFetchError) }
        )
    }
}

@Composable
private fun FriendRequestItem(
    user: User,
    onAcceptFriendRequest: (UserId) -> Unit,
    onDenyFriendRequest: (UserId) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        UserWithIcon(user)
        Row {
            IconButton(onClick = { onDenyFriendRequest(user.id) }) {
                Image(
                    painter = painterResource(R.drawable.ic_times_circle),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                    modifier = Modifier.size(40.dp),
                    contentDescription = ""

                )
            }
            IconButton(onClick = {
                onAcceptFriendRequest(user.id)
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

