package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.model.User
import dev.fredag.cheerwithme.model.UserId

class SearchService (private val userService: UserService, private val friendsService: UserFriendsService) {
    suspend fun searchUserByNick(nick: String, currentUserId: UserId): List<User> {
        val friends = friendsService.getUserFriends(currentUserId);
        return userService.searchUserByNick(nick, friends.friends.map { it.id } + currentUserId)
    }
}