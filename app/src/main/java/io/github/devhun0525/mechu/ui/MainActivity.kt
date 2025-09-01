package io.github.devhun0525.mechu

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import io.github.devhun0525.mechu.fragment.AccountFragment
import io.github.devhun0525.mechu.fragment.HeartFragment
import io.github.devhun0525.mechu.fragment.HomeFragment
import io.github.devhun0525.mechu.fragment.MapFragment
import io.github.devhun0525.mechu.ui.theme.MechuAndroidKtTheme

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Map : Screen("map", "Map", Icons.Filled.LocationOn)
    object Heart : Screen("heart", "Heart", Icons.Filled.Favorite)
    object Account : Screen("account", "Account", Icons.Filled.AccountCircle)
}


val items = listOf(
    Screen.Home,
    Screen.Map,
    Screen.Heart,
    Screen.Account,
)

class MainActivity : AppCompatActivity() {
    var log = "MainActivity"
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MechuAndroidKtTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
fun DisplayFragment(modifier: Modifier = Modifier, fragmentInstance: Fragment) {
    val context = LocalContext.current
    val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager ?: return
    val containerId = rememberSaveable { View.generateViewId() }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FragmentContainerView(context).apply { id = containerId }
        },
        update = { view ->
            fragmentManager.commit {
                replace(view.id, fragmentInstance)
            }
        },
    )
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    MechuAndroidKtTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { DisplayFragment(fragmentInstance = HomeFragment()) }
                composable(Screen.Map.route) { DisplayFragment(fragmentInstance = MapFragment()) }
                composable(Screen.Heart.route) { DisplayFragment(fragmentInstance = HeartFragment()) }
                composable(Screen.Account.route) { DisplayFragment(fragmentInstance = AccountFragment()) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MechuAndroidKtTheme {
        MainScreen()
    }
}
