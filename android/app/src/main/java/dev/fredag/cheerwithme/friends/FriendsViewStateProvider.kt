package dev.fredag.cheerwithme.friends

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.data.backend.UserFriends

class FriendsViewStateProvider : PreviewParameterProvider<FriendsViewState> {
    override val values = sequenceOf(
        FriendsViewState(
            friendsModel = UserFriends(
                friends = listOf(User(1, "John", null)),
                incomingFriendRequests = listOf(User(2, "Eve", null)),
                outgoingFriendRequests = listOf(User(3, "Bob", null))
            )
        )
    )
}
