package com.example.chat.pages

import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.MarkUnreadChatAlt
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.components.RoundedCornerCard
import com.example.chat.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun HomePage(navController: NavController) {
    val profile = remember {
        mutableStateOf(
            Account(
                "",
                "",
                "https://cdn3.iconfinder.com/data/icons/seo-colored-flat-easy/128/Custom_user_profile_Account-512.png"
            )
        )
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
            DrawerContent(profile) { scope.launch { drawerState.close() } }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),

            ) { padding ->
            Box(
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
                                        scaleType = ImageView.ScaleType.FIT_XY }},
                                    update = {imageView->
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
        }
    }
}

@Composable
fun DrawerContent(profile: MutableState<Account>, onCloseDrawer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Person",
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
    }
}
