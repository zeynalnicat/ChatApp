package com.example.chat.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(navController: NavController){
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Chat") })
        }
    ) {innerPadding->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

        }

    }
}