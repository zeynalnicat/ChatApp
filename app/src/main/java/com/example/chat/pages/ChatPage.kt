package com.example.chat.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(navController: NavController) {
    val message = remember { mutableStateOf(TextFieldValue()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .clickable { navController.popBackStack() }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Sandra Dorsett", fontWeight = FontWeight.Bold)
                                Text(text = "Seen 1 hour ago", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                        }

                    }

                },
                actions = {

                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach File")
                }

                BasicTextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(
                            Color(0xFFF1F1F1),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (message.value.text.isEmpty()) {
                                Text(text = "Send a message", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    }
                )
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message")
                }
            }

        }
    }
}
