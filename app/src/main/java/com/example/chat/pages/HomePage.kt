package com.example.chat.pages

import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.MarkUnreadChatAlt
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.components.Loading
import com.example.chat.components.RoundedCornerCard
import com.example.chat.models.Account
import com.example.chat.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomePage(navController: NavController) {
    val chats = remember {
        mutableStateListOf<Chat>()
    }
    val isLoading = remember {
        mutableStateOf(false)
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid


    val profile = remember {
        mutableStateOf(
            Account(
                "",
                "",
                "https://cdn3.iconfinder.com/data/icons/seo-colored-flat-easy/128/Custom_user_profile_Account-512.png"
            )
        )
    }

    LaunchedEffect(Unit) {
        isLoading.value = true
        getChats(userId!!, chats, isLoading)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val firebaseAuth = FirebaseAuth.getInstance()
    val ref = FirebaseFirestore.getInstance().collection("users")

    LaunchedEffect(Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            ref.whereEqualTo("userId", currentUser.uid).get()
                .addOnSuccessListener { documents ->
                    if (documents.documents.isNotEmpty()) {
                        val document = documents.documents[0]
                        val userId = document.getString("userId") ?: ""
                        val username = document.getString("username") ?: ""
                        val profileUrl = document.getString("profile")
                            ?: "https://cdn3.iconfinder.com/data/icons/seo-colored-flat-easy/128/Custom_user_profile_Account-512.png"
                        profile.value = Account(userId, username, profileUrl)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error fetching user data", exception)
                }
        }
    }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(profile,navController) { scope.launch { drawerState.close() } }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),

            ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                Card(
                    modifier = Modifier.padding(5.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = 60.dp)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RoundedCornerCard(innerPadding = 0.dp, modifier = Modifier.size(40.dp)) {
                            androidx.compose.material3.IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                AndroidView(factory = { context ->
                                    ImageView(context).apply {
                                        scaleType = ImageView.ScaleType.FIT_XY
                                    }
                                },
                                    update = { imageView ->
                                        Glide.with(imageView.context)
                                            .load(profile.value.profile)
                                            .into(imageView)
                                    }

                                )
                            }
                        }


                        Text(text = "Chats", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        RoundedCornerCard {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        navController.navigate("explore")
                                    },
                                colorResource(id = R.color.secondary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                if (isLoading.value) {
                    Loading()
                } else if (chats.isEmpty() && !isLoading.value) {
                    NoChat()
                } else {
                    ChatList(chats, navController)
                }

            }
        }
    }
}

@Composable
fun ChatList(chats: SnapshotStateList<Chat>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LazyColumn {
            items(chats) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.clickable {
                            navController.navigate("chat/${it.personId}")
                        }
                    ) {
                        AndroidView(modifier = Modifier.size(52.dp), factory = { context ->
                            ImageView(context).apply {
                                scaleType = ImageView.ScaleType.FIT_XY

                            }
                        },
                            update = { imageView ->
                                Glide.with(imageView.context)
                                    .load(it.profile)
                                    .into(imageView)
                            })

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = it.person,
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight(600)),
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (it.isSender) "You: ${it.lastMessage}" else it.lastMessage,
                                    color = colorResource(id = R.color.smoke_txt),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(text = it.time, color = colorResource(id = R.color.smoke_txt))
                            }
                        }
                    }

                    Divider(thickness = 0.5.dp, color = colorResource(id = R.color.smoke_txt))
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

    }
}


@Composable
fun NoChat() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.MarkUnreadChatAlt,
            contentDescription = "Chat",
            modifier = Modifier.size(100.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Let's start chatting!",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DrawerContent(
    profile: MutableState<Account>,
    navController: NavController,
    onCloseDrawer: () -> Unit,

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AndroidView(modifier = Modifier.size(64.dp), factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                },
                    update = { imageView ->

                        Glide.with(imageView.context)
                            .load(profile.value.profile)
                            .into(imageView)
                    }
                )

                Text(text = profile.value.name, fontSize = 20.sp, fontWeight = FontWeight(600))

            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("explore")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    Color.Gray

                )

                Text(text = "New Direct Message")

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Person",
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    Color.Gray

                )

                Text(text = "New Group")
            }

            Button(
                onClick = { logOut(navController) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colorResource(
                        id = R.color.secondary
                    )
                )
            ) {
                androidx.compose.material3.Text(text = "Log Out", color = Color.White)
            }
        }



    }
}

fun getChats(userId: String, chats: SnapshotStateList<Chat>, isLoading: MutableState<Boolean>) {
    val ref = FirebaseFirestore.getInstance().collection("channels")

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val query = ref.whereEqualTo("user1", userId).get().await()
            val query2 = ref.whereEqualTo("user2", userId).get().await()

            if (query.documents.isNotEmpty()) {
                for (doc in query.documents) {
                    val user2 = doc.getString("user2") ?: continue
                    val mp = getOtherUser(user2)
                    val profile = mp["profile"] as? String ?: ""
                    val messages = doc.get("messages") as? List<Map<String, Any>> ?: emptyList()
                    if (messages.isNotEmpty()) {
                        val lastMessage = messages.last()["message"] as? String ?: ""
                        val sender = messages.last()["senderId"] as? String ?: ""
                        val name = mp["username"] as? String ?: ""
                        val time = messages.last()["time"] as? String ?: ""
                        val chat = Chat(user2, name, profile, lastMessage, time, userId == sender)
                        chats.add(chat)
                    }
                }
            }

            if (query2.documents.isNotEmpty()) {
                for (doc in query2.documents) {
                    val user2 = doc.getString("user1") ?: continue
                    val mp = getOtherUser(user2)
                    val profile = mp["profile"] as? String ?: ""
                    val messages = doc.get("messages") as? List<Map<String, Any>> ?: emptyList()
                    if (messages.isNotEmpty()) {
                        val lastMessage = messages.last()["message"] as? String ?: ""
                        val sender = messages.last()["senderId"] as? String ?: ""
                        val name = mp["username"] as? String ?: ""
                        val time = messages.last()["time"] as? String ?: ""
                        val chat = Chat(user2, name, profile, lastMessage, time, userId == sender)
                        chats.add(chat)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading.value = false
        }
    }
}


suspend fun getOtherUser(userId: String): Map<String, Any> {
    val ref = FirebaseFirestore.getInstance().collection("users")
    val query = ref.whereEqualTo("userId", userId).get().await()

    if (query.documents.isNotEmpty()) {
        return query.documents[0].data!!
    }

    return mapOf()

}

fun logOut(navController: NavController) {
    val auth = FirebaseAuth.getInstance().signOut()
    navController.navigate("register"){popUpTo("register"){inclusive=true} }
}
