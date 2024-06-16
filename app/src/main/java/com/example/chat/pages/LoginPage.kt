package com.example.chat.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chat.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController) {
    val email = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ){ innerPadding ->


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
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.elevatedCardColors(colorResource(id = R.color.smoke))
                ) {
                    TextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        placeholder = {
                            Text("Password")
                        },
                        shape = RoundedCornerShape(4.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
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
                    text = "Haven't an account? Register ",
                    color = colorResource(id = R.color.secondary),
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )

            }

            Button(
                onClick = { login(email.value, password.value, navController,snackbarHostState) },
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
                Text(text = "Login", color = Color.White)
            }

        }
    }
}

fun login(email: String, password: String, navController: NavController,snackbarHostState: SnackbarHostState) {


    if(email.isEmpty() || password.isEmpty()){
        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar("Fill the fields , please")
        }

    }

    else{
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                navController.navigate("home"){popUpTo("home"){inclusive=true} }
            }else{
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("Failed to login! ")
                }

            }
        }
    }

}