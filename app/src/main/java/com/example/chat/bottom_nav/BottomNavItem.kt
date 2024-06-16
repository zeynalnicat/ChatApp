package com.example.chat.bottom_nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Outlined.Chat, "Chats")
    object Mention : BottomNavItem("mentions",Icons.Outlined.Tag, "Mentions")

}