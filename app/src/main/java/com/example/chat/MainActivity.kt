package com.example.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chat.bottom_nav.BottomNavItem
import com.example.chat.pages.ExplorePage
import com.example.chat.pages.HomePage
import com.example.chat.pages.LoginPage
import com.example.chat.pages.MentionPage
import com.example.chat.pages.RegisterPage
import com.example.chat.ui.theme.ChatTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val firebaseAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { if(firebaseAuth.currentUser!=null) BottomNavigationBar(navController = navController) }) { it
                    NavigationController(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NavigationController(navController: NavHostController) {
    val firebaseAuth = FirebaseAuth.getInstance()

    NavHost(navController = navController, startDestination = if(firebaseAuth.currentUser!=null) BottomNavItem.Home.route else "register") {
        composable(BottomNavItem.Home.route) {
            HomePage(navController = navController)
        }

        composable(BottomNavItem.Mention.route) {
            MentionPage(navController = navController)
        }
        
        composable("register"){
            RegisterPage(navController = navController)
        }
        
        composable("login"){
            LoginPage(navController = navController)
        }

        composable("explore"){
            ExplorePage(navController = navController)
        }
    }

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val listBottom =
        listOf(BottomNavItem.Home, BottomNavItem.Mention)

    BottomNavigation(
        backgroundColor = Color.White,
        modifier = Modifier.height(height = 80.dp),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                listBottom.forEach { item ->
                    BottomNavigationItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                modifier = Modifier.size(width = 32.dp, height = 32.dp),
                                tint = if (currentRoute == item.route) Color.Black else Color.Gray

                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 16.sp,
                                color = Color.Gray,
                            )
                        },
                        selectedContentColor = Color.Gray,
                        unselectedContentColor = Color.Gray
                    )
                }
            }
        }
    )
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    ChatTheme {
        NavigationController(navController =navController)
    }
}