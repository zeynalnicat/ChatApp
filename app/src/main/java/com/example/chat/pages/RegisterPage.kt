package com.example.chat.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController) {
    val email = remember {
        mutableStateOf("")
    }

    val name = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }

    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),

            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                Card(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.elevatedCardColors(colorResource(id = R.color.smoke))
                ) {
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        placeholder = {
                            Text("Username")
                        },
                        shape = RoundedCornerShape(4.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = Color.Black,
                            containerColor = Color.Transparent,
                            unfocusedTextColor = Color.Black,
                            cursorColor = colorResource(id = R.color.secondary),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedPlaceholderColor = colorResource(id = R.color.smoke_txt)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                Card(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.elevatedCardColors(colorResource(id = R.color.smoke))
                ) {
                    TextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        placeholder = {
                            Text("E-mail")
                        },
                        shape = RoundedCornerShape(4.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = Color.Black,
                            containerColor = Color.Transparent,
                            unfocusedTextColor = Color.Black,
                            cursorColor = colorResource(id = R.color.secondary),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedPlaceholderColor = colorResource(id = R.color.smoke_txt)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                Card(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
                    colors = CardDefaults.elevatedCardColors(colorResource(id = R.color.smoke))
                ) {
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        placeholder = { Text(text = "Password") },
                        shape = RoundedCornerShape(4.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = Color.Black,
                            containerColor = Color.Transparent,
                            unfocusedTextColor = Color.Black,
                            cursorColor = colorResource(id = R.color.secondary),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedPlaceholderColor = colorResource(id = R.color.smoke_txt)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                Text(
                    text = "Have an account? Login ",
                    color = colorResource(id = R.color.secondary),
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }

            Button(
                onClick = { register(email.value,name.value,password.value,navController,snackbarHostState) },
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
                Text(text = "Register", color = Color.White)
            }

        }

    }
}

fun register(email: String, username: String, password: String,navController: NavController,snackbarHostState: SnackbarHostState) {

    if(email.isEmpty() || username.isEmpty() || password.isEmpty() ){
        CoroutineScope(Dispatchers.Main).launch{
            snackbarHostState.showSnackbar("Fill the fields , please")
        }
    }

    else{

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val ref = firestore.collection("users")

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val userId = firebaseAuth.currentUser?.uid ?: ""
                val info = hashMapOf(
                    "profile" to "https://cdn3.iconfinder.com/data/icons/seo-colored-flat-easy/128/Custom_user_profile_Account-512.png",
                    "userId" to userId,
                    "username" to username
                )

                ref.add(info).addOnSuccessListener {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
                    .addOnFailureListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar(it.message?:"Failed to register")
                        }

                    }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("Failed to register . Please try later")
                }

            }
        }
    }



}