package dev.fredag.cheerwithme.friends

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.UserId
import dev.fredag.cheerwithme.happening.ScreenHeaderText
import dev.fredag.cheerwithme.ui.beerYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun FindFriendView(navController: NavHostController, findFriendsViewModel: FriendsViewModel) {
    val searchName = remember { mutableStateOf("") }
    val viewState by findFriendsViewModel.viewState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            findFriendsViewModel.viewEvent.collect {
                when (it) {
                    FriendsViewModelEvent.AddFriendFailure -> Toast.makeText(
                        context,
                        "Failed to add friend, fuck off.",
                        Toast.LENGTH_SHORT
                    ).show()
                    FriendsViewModelEvent.FetchFriendsFailed -> Toast.makeText(
                        context,
                        "Failed to fetch friends.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Surface(modifier = Modifier.padding(20.dp, 16.dp)) {
        Column() {
            ScreenHeaderText("Friends")
            Row() {
                TextField(
                    value = searchName.value,
                    onValueChange = {
                        searchName.value = it
                        findFriendsViewModel.searchUserByNick(it) // TODO debounce
                    }, label = {
                        Text(
                            text = "Search for friends"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(onDone = {
                        findFriendsViewModel.searchUserByNick(searchName.value)
                    }),
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Clear,
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    searchName.value = ""
                                    // TODO Clear search results, and handle this flow better.
                                },
                            contentDescription = "Clear"
                        )
                    }
                )
            }

            viewState.usersMatchingSearch.let {
                UsersList(it) { userid ->
                    findFriendsViewModel.sendAction(FriendsViewActions.SendFriendRequest(userid))
                }
            } ?: Text(text = "Loading")
        }
    }
}

@Composable
private fun UsersList(
    users: List<UserWithFriendStatus> = listOf(),
    sendFriendRequest: (UserId) -> Unit,
) {
    LazyColumn() {
        users.forEach {
            item {
                FriendResultSearchRowItem(it, sendFriendRequest)
            }

        }
    }
}

@Composable
private fun FriendResultSearchRowItem(
    it: UserWithFriendStatus,
    sendFriendRequest: (UserId) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        UserWithIcon(it.user)
        Row {
            if (it.loading) {
                CircularProgressIndicator()
            } else {
                IconButton(onClick = {
                    sendFriendRequest(it.user.id)
                }) {
                    var modifier = Modifier
                        .size(40.dp)
                        .padding(10.dp)
                    var tint = ColorFilter.tint(MaterialTheme.colors.secondary)
                    if (it.status == FriendStatus.Unknown || it.status == FriendStatus.ReceivedRequest) {
                        modifier = Modifier
                            .border(1.dp, color = beerYellow, shape = CircleShape)
                            .size(40.dp)
                            .padding(10.dp)
                        tint = ColorFilter.tint(MaterialTheme.colors.primary)
                    }

                    Image(
                        painter = painterResource(getResourceForFriendStatus(it)),
                        colorFilter = tint,
                        modifier = modifier,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

fun getResourceForFriendStatus(user: UserWithFriendStatus): Int {
    return when (user.status) {
        FriendStatus.IsFriend -> R.drawable.ic_beer_black_24dp
        FriendStatus.SentRequest -> R.drawable.ic_envelope_2
        FriendStatus.ReceivedRequest -> R.drawable.ic_envelope_2
        FriendStatus.Unknown -> R.drawable.ic_user_plus
    }
}
