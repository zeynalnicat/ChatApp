package com.example.chat.pages

import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.models.Account
import com.example.chat.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(navController: NavController, userId: String) {
    val ref = FirebaseFirestore.getInstance().collection("users")
    val user = remember { mutableStateOf(Account("", "", "")) }
    val message = remember { mutableStateOf(TextFieldValue()) }

    val messages = remember { mutableStateListOf<Message>() }

    val receiverId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(messages) {
        try {
            val query = ref
                .whereEqualTo("user1", userId)
                .whereEqualTo("user2", receiverId)
                .get()
                .await()

            val query2 = ref
                .whereEqualTo("user1", receiverId)
                .whereEqualTo("user2", userId)
                .get()
                .await()

            val channelDoc = query.documents.firstOrNull() ?: query2.documents.firstOrNull()

            if (channelDoc != null) {
                val datas = channelDoc["messages"] as? List<Map<String, Any>>
                val messageList = datas?.map { data ->
                    Message(
                        data["senderId"] as String,
                        data["message"] as String,
                        data["time"] as String
                    )
                }

                messageList?.let {
                    messages.clear()
                    messages.addAll(it)
                }
            } else {

                messages.clear()
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        val query = ref.whereEqualTo("userId", userId).get()
        query.addOnSuccessListener { task ->
            if (task.documents.isNotEmpty()) {
                task.documents[0].apply {
                    user.value =
                        Account(
                            get("userId") as String,
                            get("username") as String,
                            get("profile") as String
                        )

                    Log.d("ChatPage", "User: $user")
                }

            }
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .clickable { navController.popBackStack() }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AndroidView(
                                modifier = Modifier.size(42.dp),
                                factory = { context ->
                                    ImageView(context).apply {
                                        scaleType = ImageView.ScaleType.FIT_XY
                                    }
                                },
                                update = { imageView ->
                                    Glide.with(imageView.context)
                                        .load(user.value.profile)
                                        .into(imageView)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = user.value.name, fontWeight = FontWeight.Bold)

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

                LazyColumn {
                    Log.d("ChatPage", "Messages: $messages")
                    items(messages.size) {
                        if (messages[it].senderId == receiverId) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                ) {
                                    Card(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                        modifier = Modifier
                                            .background(colorResource(id = R.color.smoke_txt))
                                    ) {
                                        Text(
                                            text = messages[it].string,
                                            color = Color.Black
                                        )
                                    }
                                    Text(
                                        text = messages[it].time,
                                        color = Color.Gray
                                    )

                                }

                            }
                        }
                    }
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
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
                IconButton(onClick = { sendMessage(message, userId,messages) }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message")
                }
            }

        }
    }
}


fun sendMessage(message: MutableState<TextFieldValue>, receiverId: String , messages:SnapshotStateList<Message>) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseFirestore.getInstance().collection("channels")

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val query = ref
                .whereEqualTo("user1", userId)
                .whereEqualTo("user2", receiverId)
                .get()
                .await()

            if (query.documents.isNotEmpty()) {
                val channelId = query.documents[0].id
                Log.d("ChatPage", "Channel ID: $channelId")
                addMessageToChannel(channelId, userId, message.value.text)
                messages.add(Message(userId!!, message.value.text, getCurrentTime()))
                message.value = TextFieldValue()

            } else {
                Log.d("ChatPage", "No channel found")
                val reverseQuery = ref
                    .whereEqualTo("user1", receiverId)
                    .whereEqualTo("user2", userId)
                    .get()
                    .await()

                if (reverseQuery.documents.isNotEmpty()) {
                    val channelId = reverseQuery.documents[0].id
                    Log.d("ChatPage", "Channel ID: $channelId")
                    addMessageToChannel(channelId, userId, message.value.text)
                    messages.add(Message(userId!!, message.value.text, getCurrentTime()))
                    message.value = TextFieldValue()
                } else {
                    val channel = hashMapOf(
                        "user1" to userId,
                        "user2" to receiverId,
                        "messages" to listOf(
                            hashMapOf(
                                "senderId" to userId,
                                "message" to message.value.text,
                                "time" to getCurrentTime()
                            )
                        )
                    )
                    ref.add(channel).addOnSuccessListener {
                        message.value = TextFieldValue()
                        messages.add(Message(userId!!, message.value.text, getCurrentTime()))
                    }.addOnFailureListener {

                        Log.e("ChatPage", it.message.toString())

                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun addMessageToChannel(channelId: String, senderId: String?, messageText: String) {
    val ref = FirebaseFirestore.getInstance().collection("channels").document(channelId)

    val timestamp = getCurrentTime()

    val newMessage = hashMapOf(
        "senderId" to senderId,
        "message" to messageText,
        "time" to timestamp
    )

    ref.update("messages", FieldValue.arrayUnion(newMessage)).addOnSuccessListener {
        Log.d("ChatPage", "Message added to channel")
    }.addOnFailureListener {
        Log.e("ChatPage", it.message.toString())
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm")
    val currentTime = Date()
    return sdf.format(currentTime)
}