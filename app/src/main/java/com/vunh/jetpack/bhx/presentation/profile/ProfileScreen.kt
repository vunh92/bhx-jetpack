package com.vunh.jetpack.bhx.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.vunh.jetpack.bhx.domain.model.UserProfile
import com.vunh.jetpack.bhx.presentation.common.HeaderSection
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ProfileScreen(
    onMenuClick: () -> Unit,
    onLoginSuccess: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onScannerClick: () -> Unit = {},
    onWalletClick: (String) -> Unit = {},
    onCouponClick: () -> Unit = {},
    onSpecialOfferClick: () -> Unit = {},
    onGiftClick: () -> Unit = {},
    onPointExchangeClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userProfile
    val isLoggedIn = uiState.isLoggedIn
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { HeaderSection(isHome = false, onMenuClick = onMenuClick) },
        containerColor = Color(0xFFF0F2F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (!isLoggedIn) {
                    LoginCard(
                        isLoading = uiState.isLoading,
                        onContinueClick = viewModel::showOtpDialog,
                        onCredentialLogin = viewModel::loginWithCredentials,
                        onLoginSuccess = onLoginSuccess
                    )
                } else {
                    LoggedInContent(
                        profile = userProfile!!,
                        onLogout = {
                            viewModel.logout()
                            onLogoutSuccess()
                        },
                        onNotificationClick = onNotificationClick,
                        onScannerClick = onScannerClick,
                        onWalletClick = onWalletClick,
                        onCouponClick = onCouponClick,
                        onSpecialOfferClick = onSpecialOfferClick,
                        onGiftClick = onGiftClick,
                        onPointExchangeClick = onPointExchangeClick
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                SupportCard()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (uiState.showOtpDialog) {
        OtpDialog(
            phoneNumber = uiState.phoneNumber,
            onDismiss = viewModel::dismissOtpDialog,
            onOtpComplete = {
                viewModel.completeLogin()
                onLoginSuccess()
            }
        )
    }
}

@Composable
fun LoggedInContent(
    profile: UserProfile, 
    onLogout: () -> Unit, 
    onNotificationClick: () -> Unit,
    onScannerClick: () -> Unit,
    onWalletClick: (String) -> Unit,
    onCouponClick: () -> Unit,
    onSpecialOfferClick: () -> Unit,
    onGiftClick: () -> Unit,
    onPointExchangeClick: () -> Unit
) {
    Column {
        // Membership Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.Gray, fontSize = 12.sp)) {
                                    append("${profile.rank} ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                                    append("${String.format("%,d", profile.points).replace(',', '.')} điểm")
                                }
                            }
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onScannerClick() }
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color(0xFF008848),
                            modifier = Modifier.size(32.dp)
                        )
                        Text("Quét tích điểm", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Simulated Barcode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White)
                        .border(1.dp, Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(40) {
                            Box(modifier = Modifier.width(if (it % 3 == 0) 3.dp else 1.dp).fillMaxHeight().background(Color.Black))
                        }
                    }
                }
                Text(
                    "Đưa mã hoặc đọc số ${profile.memberCode} để tích, sử dụng điểm",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Menu List
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Thông báo",
                    badge = profile.notificationCount.toString(),
                    onClick = onNotificationClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                ProfileMenuItem(
                    icon = Icons.Default.AccountBalanceWallet, 
                    title = "Tiền Dư", 
                    amount = "${profile.walletBalance}đ",
                    onClick = { onWalletClick(profile.walletBalance.toString()) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                ProfileMenuItem(
                    icon = Icons.Default.ConfirmationNumber, 
                    title = "Phiếu mua hàng", 
                    badge = profile.couponCount.takeIf { it > 0 }?.toString(),
                    onClick = onCouponClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                ProfileMenuItem(
                    icon = Icons.Default.CardGiftcard, 
                    title = "Quà của tôi", 
                    badge = profile.giftCount.takeIf { it > 0 }?.toString(),
                    onClick = onGiftClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                ProfileMenuItem(
                    icon = Icons.Default.Stars, 
                    title = "Ưu đãi đặc biệt",
                    onClick = onSpecialOfferClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                ProfileMenuItem(
                    icon = Icons.Default.CardMembership, 
                    title = "Tích điểm đổi quà",
                    onClick = onPointExchangeClick
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal Info Section
        Text("Thông tin cá nhân", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                ProfileMenuItem(Icons.Default.Person, "Sửa thông tin cá nhân")
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                ProfileMenuItem(Icons.Default.LocationOn, "Địa chỉ nhận hàng", badge = profile.addressCount.takeIf { it > 0 }?.toString())
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Đăng xuất", color = Color.Red, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    amount: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 15.sp)
        
        if (amount != null) {
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp))
        }
        
        if (badge != null && badge != "0") {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(badge, color = Color.White, fontSize = 12.sp)
            }
        }
        
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun LoginCard(
    isLoading: Boolean = false,
    onContinueClick: (String) -> Unit,
    onCredentialLogin: (String, String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isValidPhone = phoneNumber.length == 10 && phoneNumber.startsWith("0") && phoneNumber.all { it.isDigit() }
    val isValidCredentials = username.trim().isNotEmpty() && password.isNotBlank()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thông tin cá nhân", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                buildAnnotatedString {
                    append("Mời Anh/Chị ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("đăng nhập") }
                    append(" để đặt hàng nhanh chóng và thuận tiện hơn")
                },
                fontSize = 14.sp, color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            val tabs = listOf("Số điện thoại", "Tài khoản")
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFF7F9FB),
                contentColor = Color(0xFF008848),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF008848)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (selectedTabIndex == 0) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Số điện thoại", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isValidPhone) Color(0xFF008848) else Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onContinueClick(phoneNumber) },
                    enabled = isValidPhone && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF008848),
                        disabledContainerColor = Color(0xFF9FA8B8),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) { 
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Tiếp tục", fontWeight = FontWeight.Bold) 
                    }
                }
            } else {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Username", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (username.isNotBlank()) Color(0xFF008848) else Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (password.isNotBlank()) Color(0xFF008848) else Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onCredentialLogin(username, password)
                    },
                    enabled = isValidCredentials && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF008848),
                        disabledContainerColor = Color(0xFF9FA8B8),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) { 
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Đăng nhập", fontWeight = FontWeight.Bold) 
                    }
                }
            }
        }
    }
}

@Composable
fun OtpDialog(phoneNumber: String, onDismiss: () -> Unit, onOtpComplete: () -> Unit) {
    var otpValue by remember { mutableStateOf("") }
    var ticks by remember { mutableIntStateOf(60) }
    var isVerifying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (ticks > 0) {
            delay(1000L)
            ticks--
        }
    }

    // Tự động gọi API khi đủ 6 ký tự
    LaunchedEffect(otpValue) {
        if (otpValue.length == 6) {
            isVerifying = true
            delay(1500L) // Giả lập call API
            onOtpComplete()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = buildAnnotatedString {
                            append("Nhập 6 số được gửi vào ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Tin nhắn SMS") }
                            append("\ncủa số điện thoại ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(phoneNumber) }
                        },
                        textAlign = TextAlign.Center, fontSize = 15.sp, lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = otpValue,
                        onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otpValue = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp).background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp)),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 8.sp, color = Color(0xFF3F51B5)),
                        placeholder = { Text("|", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF3F51B5), fontSize = 24.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    val timeString = String.format(Locale.getDefault(), "%02d:%02d", ticks / 60, ticks % 60)
                    Text(
                        text = buildAnnotatedString {
                            append("Nếu không nhận được tin nhắn,\nchọn ")
                            withStyle(style = SpanStyle(color = if (ticks == 0) Color(0xFF008848) else Color.Gray, fontWeight = FontWeight.Bold)) { append("Gửi lại mã xác thực sau ") }
                            withStyle(style = SpanStyle(color = Color(0xFF008848), fontWeight = FontWeight.Bold)) { append(timeString) }
                        },
                        textAlign = TextAlign.Center, fontSize = 14.sp, lineHeight = 18.sp
                    )
                    if (isVerifying) {
                        Box(modifier = Modifier.fillMaxWidth().padding(8.dp).size(20.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF008848))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Hỗ trợ khách hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Phiên bản: 2.0.33 (v1121)", color = Color.Gray, fontSize = 12.sp)
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))
            SupportItem(Icons.Default.Call, "Tư vấn: 1900.1908", "(7:30 - 21:00)")
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEEEEEE))
            SupportItem(Icons.Default.Call, "Khiếu nại: 1800.1067", "(7:30 - 21:00) ", "Miễn phí")
            HorizontalDivider(color = Color(0xFFEEEEEE))
            SupportItemWithArrow(Icons.Default.Place, "Tìm kiếm cửa hàng")
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEEEEEE))
            SupportItemWithArrow(Icons.Default.Info, "Các chính sách khác")
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEEEEEE))
            SupportItemWithArrow(Icons.Default.Refresh, "Cập nhật ứng dụng")
        }
    }
}

@Composable
fun SupportItem(icon: ImageVector, title: String, subtitle: String, highlight: String? = null) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = buildAnnotatedString { append(title); append(" "); withStyle(style = SpanStyle(color = Color.Gray)) { append(subtitle) }; if (highlight != null) { withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) { append(highlight) } } }, fontSize = 14.sp)
    }
}

@Composable
fun SupportItemWithArrow(icon: ImageVector, title: String) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 14.sp)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Black)
    }
}
