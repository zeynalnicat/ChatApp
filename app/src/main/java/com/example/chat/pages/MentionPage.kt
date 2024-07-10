package com.example.chat.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun MentionPage(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("explore/${true}") },
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
                modifier = Modifier.padding(vertical = 60.dp,))
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = "Mentions")
        }

    }
}