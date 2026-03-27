package com.vunh.jetpack.bhx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.presentation.cart.CartScreen
import com.vunh.jetpack.bhx.presentation.category.CategoryScreen
import com.vunh.jetpack.bhx.presentation.history.OrderHistoryScreen
import com.vunh.jetpack.bhx.presentation.home.HomeScreen
import com.vunh.jetpack.bhx.presentation.profile.CouponScreen
import com.vunh.jetpack.bhx.presentation.profile.GiftScreen
import com.vunh.jetpack.bhx.presentation.profile.NotificationScreen
import com.vunh.jetpack.bhx.presentation.profile.PointExchangeScreen
import com.vunh.jetpack.bhx.presentation.profile.ProfileScreen
import com.vunh.jetpack.bhx.presentation.profile.ScannerScreen
import com.vunh.jetpack.bhx.presentation.profile.SpecialOfferScreen
import com.vunh.jetpack.bhx.presentation.profile.WalletScreen
import com.vunh.jetpack.bhx.ui.theme.BhxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BhxTheme {
                BhxApp()
            }
        }
    }
}

@Composable
fun BhxApp() {
    val context = LocalContext.current
    val profileManager = remember { ProfileManager(context) }
    val navController = rememberNavController()
    
    var isMenuOpen by rememberSaveable { mutableStateOf(false) }
    
    // Track login state to update UI
    var loginTrigger by remember { mutableIntStateOf(0) }
    val userProfile = remember(loginTrigger) { profileManager.getProfile() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = AppDestinations.entries.any { it.route == currentRoute }
    val navigateToTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (isMenuOpen) {
        CategoryScreen(
            onClose = { isMenuOpen = false },
            onHomeClick = { 
                isMenuOpen = false
                navigateToTopLevel(AppDestinations.HOME.route)
            }
        )
    } else if (showBottomBar) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (destination == AppDestinations.PROFILE && userProfile != null && userProfile.notificationCount > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White,
                                            modifier = Modifier.clip(CircleShape)
                                        ) {
                                            Text(userProfile.notificationCount.toString(), fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            }
                        },
                        label = { 
                            val label = if (destination == AppDestinations.PROFILE && userProfile != null) {
                                userProfile.name.split(" ").lastOrNull()?.let { "A.$it" } ?: userProfile.name
                            } else {
                                destination.label
                            }
                            Text(label) 
                        },
                        selected = currentRoute == destination.route,
                        onClick = { 
                            navigateToTopLevel(destination.route)
                        }
                    )
                }
            }
        ) {
            BhxNavHost(
                navController = navController,
                isLoggedIn = userProfile != null,
                onMenuClick = { isMenuOpen = true },
                onLoginSuccess = { loginTrigger++ },
                onNavigateToProfile = { navigateToTopLevel(AppDestinations.PROFILE.route) }
            )
        }
    } else {
        BhxNavHost(
            navController = navController,
            isLoggedIn = userProfile != null,
            onMenuClick = { isMenuOpen = true },
            onLoginSuccess = { loginTrigger++ },
            onNavigateToProfile = { navigateToTopLevel(AppDestinations.PROFILE.route) }
        )
    }
}

@Composable
fun BhxNavHost(
    navController: androidx.navigation.NavHostController,
    isLoggedIn: Boolean,
    onMenuClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppDestinations.HOME.route) {
            HomeScreen(
                onMenuClick = onMenuClick,
                onNavigateToProfile = onNavigateToProfile,
                isLoggedIn = isLoggedIn
            )
        }
        composable(AppDestinations.HISTORY.route) {
            OrderHistoryScreen(
                onMenuClick = onMenuClick,
                onNavigateToLogin = onNavigateToProfile
            )
        }
        composable(AppDestinations.CART.route) {
            CartScreen(
                onMenuClick = onMenuClick,
                onNavigateToLogin = onNavigateToProfile
            )
        }
        composable(AppDestinations.PROFILE.route) {
            ProfileScreen(
                onMenuClick = onMenuClick,
                onLoginSuccess = onLoginSuccess,
                onNotificationClick = { navController.navigate("notifications") },
                onScannerClick = { navController.navigate("scanner") },
                onWalletClick = { balance -> navController.navigate("wallet/$balance") },
                onCouponClick = { navController.navigate("coupons") },
                onSpecialOfferClick = { navController.navigate("special-offers") },
                onGiftClick = { navController.navigate("gifts") },
                onPointExchangeClick = { navController.navigate("point-exchange") }
            )
        }
        composable("notifications") {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
        composable("scanner") {
            ScannerScreen(
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
        composable(
            route = "wallet/{balance}",
            arguments = listOf(navArgument("balance") { type = NavType.StringType })
        ) { backStackEntry ->
            val balance = backStackEntry.arguments?.getString("balance") ?: "0"
            WalletScreen(
                balance = balance,
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
        composable("coupons") {
            CouponScreen(
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
        composable("special-offers") {
            SpecialOfferScreen(
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
        composable("gifts") {
            GiftScreen(
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
        composable("point-exchange") {
            PointExchangeScreen(
                onBack = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineLarge)
    }
}

enum class AppDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME("home", "Trang chủ", Icons.Default.Home),
    HISTORY("history", "Đơn hàng", Icons.Default.ReceiptLong),
    CART("cart", "Giỏ hàng", Icons.Default.ShoppingCart),
    PROFILE("profile", "Tài khoản", Icons.Default.AccountBox),
}
