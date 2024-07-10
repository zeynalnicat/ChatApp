package com.example.chat.pages

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorePage(navController: NavController, isGroup: Boolean) {
    val search = remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val selectedUsers = remember { mutableStateOf(mutableListOf<Account>()) }
    val ref = FirebaseFirestore.getInstance().collection("users")
    val users = remember { mutableStateOf(listOf<Account>()) }

    LaunchedEffect(search.value) {

        // try {
        //     if (search.value.isEmpty()) {
        //         getUsers(ref = ref, userId = userId, users = users)
        //     } else {
        //         val searchText = search.value.lowercase().trim()
        //         val searchedList = users.value.filter { it.name.lowercase().contains(searchText) }
        //         users.value = searchedList
        //     }
        // } catch (e: Exception) {
        //     e.printStackTrace()
        // }
    }

    LaunchedEffect(Unit) {
        getUsers(ref = ref, userId = userId, users = users)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
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
                }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .height(60.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        Modifier.size(40.dp)
                    )
                    TextField(
                        value = search.value,
                        modifier = Modifier.fillMaxSize(),
                        onValueChange = { search.value = it },
                        placeholder = { Text(text = "Search") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                items(users.value.size) { index ->
                    val user = users.value[index]
                    var isChecked by remember { mutableStateOf(user.isChecked) }
                    UserRow(
                        user = user,
                        isGroup = isGroup,
                        navController = navController,
                        isChecked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            users.value = users.value.toMutableList().apply {
                                this[index] = this[index].copy(isChecked = isChecked)
                            }
                            if (it) {
                                selectedUsers.value.add(user)
                            } else {
                                selectedUsers.value.remove(user)
                            }
                        }
                    )
                }
            }

            if (selectedUsers.value.isNotEmpty()) {
                Button(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}

@Composable
fun UserRow(
    user: Account,
    isGroup: Boolean,
    navController: NavController,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.clickable {
            if (isGroup) {
                val newCheckedState = !isChecked
                onCheckedChange(newCheckedState)
            } else {
                navController.navigate("chat/${user.id}")
            }
        }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AndroidView(
                    modifier = Modifier.size(52.dp),
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.FIT_XY
                        }
                    },
                    update = { imageView ->
                        Glide.with(imageView.context)
                            .load(user.profile)
                            .into(imageView)
                    }
                )
                Column(modifier = Modifier.fillMaxHeight()) {
                    Text(text = user.name, fontWeight = FontWeight(600))
                }
            }

            if (isGroup) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onCheckedChange(it) }
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.6f),
            color = colorResource(id = R.color.smoke_txt),
            thickness = 0.5.dp,
        )
    }
}

private fun getUsers(
    ref: CollectionReference,
    userId: String?,
    users: MutableState<List<Account>>
) {
    val query = ref.whereNotEqualTo("userId", userId)

    query.get().addOnSuccessListener { task ->
        if (task.documents.isNotEmpty()) {
            users.value = task.documents.map {
                Account(
                    it.get("userId") as String,
                    it.get("username") as String,
                    it.get("profile") as String
                )
            }
        }
    }
}
