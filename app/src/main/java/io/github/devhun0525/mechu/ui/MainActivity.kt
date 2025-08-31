package io.github.devhun0525.mechu

import android.os.Bundle
import android.view.View // Added for View.generateViewId()
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember // Added for remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext // Added for LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView // Added for AndroidView
import androidx.fragment.app.Fragment // Added for Fragment
import androidx.fragment.app.FragmentContainerView // Added for FragmentContainerView
import androidx.fragment.app.commit // Added for commit
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
                MainScreen(modifier = Modifier.fillMaxSize())
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
    val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
        ?: return // Early return if context is not an AppCompatActivity or fragmentManager is null

    // It's important to use a stable and unique ID for the FragmentContainerView
    // if multiple instances of DisplayFragment might be composed simultaneously
    // with different fragments. For NavHost, this specific remember might not be strictly
    // necessary as NavHost manages recomposition, but it's good practice.
    val containerId = remember { View.generateViewId() }

    AndroidView(
        factory = { ctx ->
            FragmentContainerView(ctx).apply { id = containerId }
        },
        update = { view ->
            fragmentManager.commit {
                replace(view.id, fragmentInstance)
            }
        },
        modifier = modifier
    )
}

@Composable
fun MainScreen(modifier: Modifier) {
    val navController = rememberNavController()

    MechuAndroidKtTheme {
        Scaffold(
            modifier = modifier,
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
                            }
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
                composable(Screen.Home.route) { DisplayFragment(modifier = Modifier.fillMaxSize(), fragmentInstance = HomeFragment()) }
                composable(Screen.Map.route) { DisplayFragment(modifier = Modifier.fillMaxSize(), fragmentInstance = MapFragment()) }
                composable(Screen.Heart.route) { DisplayFragment(modifier = Modifier.fillMaxSize(), fragmentInstance = HeartFragment()) }
                composable(Screen.Account.route) { DisplayFragment(modifier = Modifier.fillMaxSize(), fragmentInstance = AccountFragment()) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MechuAndroidKtTheme {
        // Previewing MainScreen with actual fragments might be complex or not fully work
        // as fragments have their own lifecycle and context requirements.
        // Consider creating a simpler preview or a preview that mocks fragment content.
        Text("MainScreen Preview (Fragment display might not work here)")
    }
}
