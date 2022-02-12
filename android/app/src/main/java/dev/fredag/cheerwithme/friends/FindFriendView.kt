package dev.fredag.cheerwithme.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.data.backend.UserId
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun FindFriendView(navController: NavHostController, findFriendsViewModel: FriendsViewModel) {
    val searchName = remember { mutableStateOf("") }
    val usersSearchResult = findFriendsViewModel.usersMatchingSearch.observeAsState()

    Column() {
        Row() {

            Image(
                painter = painterResource(R.drawable.ic_left_arrow),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                contentDescription = ""
            )
            TextField(value = searchName.value, onValueChange = {
                searchName.value = it
                findFriendsViewModel.searchUserByNick(it) // TODO debounce
            }, label = {
                Text(
                    text = "Search for friends"
                )
            })
        }

        usersSearchResult.value?.let {
            UsersList(it) {
                findFriendsViewModel.sendFriendRequest(it)
            }
        } ?: Text(text = "Loading")
    }


}

@Composable
private fun UsersList(
    users: List<User> = listOf(),
    sendFriendRequest: (UserId) -> Unit,
) {
    Column() {
        users.forEach {
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
                        sendFriendRequest(it.id)
                    }) {
                        Image(
                            painter = painterResource(R.drawable.ic_friend_add_24dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                            modifier = Modifier.size(40.dp),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}
