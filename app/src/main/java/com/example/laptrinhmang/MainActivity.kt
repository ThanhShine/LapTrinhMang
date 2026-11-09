package com.example.laptrinhmang

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.laptrinhmang.core.network.SessionEvent
import com.example.laptrinhmang.core.network.SessionManager
import com.example.laptrinhmang.core.ui.navigation.DauBottomNavigationBar
import com.example.laptrinhmang.core.ui.navigation.NotificationNavigator
import com.example.laptrinhmang.core.ui.theme.DauTheme
import com.example.laptrinhmang.feature.account.ui.forgotpassword.ForgotPasswordScreen
import com.example.laptrinhmang.feature.account.ui.forgotpassword.ForgotPasswordViewModel
import com.example.laptrinhmang.feature.account.ui.login.LoginScreen
import com.example.laptrinhmang.feature.account.ui.login.LoginViewModel
import com.example.laptrinhmang.feature.account.ui.notifications.NotificationScreen
import com.example.laptrinhmang.feature.account.ui.notifications.NotificationViewModel
import com.example.laptrinhmang.feature.account.ui.otp.OtpScreen
import com.example.laptrinhmang.feature.account.ui.otp.OtpViewModel
import com.example.laptrinhmang.feature.account.ui.profile.ProfileScreen
import com.example.laptrinhmang.feature.account.ui.profile.ProfileViewModel
import com.example.laptrinhmang.feature.account.ui.register.RegisterScreen
import com.example.laptrinhmang.feature.account.ui.register.RegisterViewModel
import com.example.laptrinhmang.feature.admin.ui.AdminHomeScreen
import com.example.laptrinhmang.feature.admin.ui.audit.AdminAuditLogViewModel
import com.example.laptrinhmang.feature.admin.ui.departments.AdminDepartmentsViewModel
import com.example.laptrinhmang.feature.admin.ui.roles.AdminRolesViewModel
import com.example.laptrinhmang.feature.admin.ui.users.AdminUsersViewModel
import com.example.laptrinhmang.feature.approval.ApprovalScreen
import com.example.laptrinhmang.feature.booking.BookingScreen
import com.example.laptrinhmang.feature.checkin.CheckInScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DauTheme {
                DauRmsApp(sessionManager = sessionManager)
            }
        }
    }
}

@Composable
fun DauRmsApp(
    sessionManager: SessionManager,
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Observe global session events (401, 403, 409)
    LaunchedEffect(Unit) {
        sessionManager.sessionEvents.collect { event ->
            when (event) {
                is SessionEvent.Unauthorized -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Phiên làm việc hết hạn. Vui lòng đăng nhập lại.")
                    }
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SessionEvent.Forbidden -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                is SessionEvent.Conflict -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileUiState by profileViewModel.uiState.collectAsState()
    val currentUser = profileUiState.user

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainScreenRoute = currentRoute in listOf("booking", "approval", "checkin", "admin", "account")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isMainScreenRoute) {
                DauBottomNavigationBar(
                    user = currentUser,
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Auth routes
            composable("login") {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val uiState by loginViewModel.uiState.collectAsState()

                LoginScreen(
                    uiState = uiState,
                    onIdentifierChanged = loginViewModel::onIdentifierChanged,
                    onPasswordChanged = loginViewModel::onPasswordChanged,
                    onLoginClick = loginViewModel::login,
                    onQuickFillRole = loginViewModel::fillMockRoleCredentials,
                    onNavigateToRegister = { navController.navigate("register") },
                    onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                    onLoginSuccess = { user ->
                        val startRoute = when (user.role) {
                            com.example.laptrinhmang.core.model.Role.ADMIN -> "admin"
                            com.example.laptrinhmang.core.model.Role.ROOM_MANAGER -> "checkin"
                            else -> "booking"
                        }
                        navController.navigate(startRoute) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                val registerViewModel: RegisterViewModel = hiltViewModel()
                val uiState by registerViewModel.uiState.collectAsState()

                RegisterScreen(
                    uiState = uiState,
                    onUserCodeChanged = registerViewModel::onUserCodeChanged,
                    onFullNameChanged = registerViewModel::onFullNameChanged,
                    onEmailChanged = registerViewModel::onEmailChanged,
                    onPasswordChanged = registerViewModel::onPasswordChanged,
                    onConfirmPasswordChanged = registerViewModel::onConfirmPasswordChanged,
                    onRegisterClick = registerViewModel::register,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToOtp = { email ->
                        navController.navigate("otp/$email")
                    }
                )
            }

            composable(
                route = "otp/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                val otpViewModel: OtpViewModel = hiltViewModel()
                LaunchedEffect(email) {
                    otpViewModel.init(email)
                }
                val uiState by otpViewModel.uiState.collectAsState()

                OtpScreen(
                    uiState = uiState,
                    onOtpCodeChanged = otpViewModel::onOtpCodeChanged,
                    onVerifyClick = otpViewModel::verifyOtp,
                    onResendClick = otpViewModel::resendOtp,
                    onNavigateBack = { navController.popBackStack() },
                    onVerifySuccess = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("forgot_password") {
                val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
                val uiState by forgotPasswordViewModel.uiState.collectAsState()

                ForgotPasswordScreen(
                    uiState = uiState,
                    onEmailChanged = forgotPasswordViewModel::onEmailChanged,
                    onOtpCodeChanged = forgotPasswordViewModel::onOtpCodeChanged,
                    onNewPasswordChanged = forgotPasswordViewModel::onNewPasswordChanged,
                    onConfirmPasswordChanged = forgotPasswordViewModel::onConfirmPasswordChanged,
                    onRequestOtpClick = forgotPasswordViewModel::requestOtp,
                    onResetPasswordClick = forgotPasswordViewModel::resetPassword,
                    onNavigateBack = { navController.popBackStack() },
                    onResetSuccess = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // Main feature routes
            composable("booking") {
                BookingScreen()
            }

            composable("approval") {
                ApprovalScreen()
            }

            composable("checkin") {
                CheckInScreen()
            }

            composable("admin") {
                val adminUsersViewModel: AdminUsersViewModel = hiltViewModel()
                val usersUiState by adminUsersViewModel.uiState.collectAsState()

                val adminRolesViewModel: AdminRolesViewModel = hiltViewModel()
                val rolesUiState by adminRolesViewModel.uiState.collectAsState()

                val adminDepartmentsViewModel: AdminDepartmentsViewModel = hiltViewModel()
                val deptsUiState by adminDepartmentsViewModel.uiState.collectAsState()

                val adminAuditLogViewModel: AdminAuditLogViewModel = hiltViewModel()
                val auditUiState by adminAuditLogViewModel.uiState.collectAsState()

                AdminHomeScreen(
                    usersUiState = usersUiState,
                    onSearchUsersChanged = adminUsersViewModel::onSearchQueryChanged,
                    onOpenAddUserDialog = adminUsersViewModel::openAddDialog,
                    onCloseAddUserDialog = adminUsersViewModel::closeAddDialog,
                    onCreateUser = adminUsersViewModel::createUser,
                    onToggleLockUser = adminUsersViewModel::toggleLockUser,
                    onOpenResetPasswordDialog = adminUsersViewModel::openResetPasswordDialog,
                    onCloseResetPasswordDialog = adminUsersViewModel::closeResetPasswordDialog,
                    onResetPassword = adminUsersViewModel::resetPassword,

                    rolesUiState = rolesUiState,
                    onPermissionToggle = adminRolesViewModel::updateRolePermission,

                    deptsUiState = deptsUiState,
                    onOpenAddDeptDialog = adminDepartmentsViewModel::openAddDialog,
                    onCloseAddDeptDialog = adminDepartmentsViewModel::closeAddDialog,
                    onCreateDepartment = adminDepartmentsViewModel::createDepartment,

                    auditUiState = auditUiState,
                    onAuditActionFilterChanged = adminAuditLogViewModel::onActionFilterChanged
                )
            }

            composable("account") {
                ProfileScreen(
                    uiState = profileUiState,
                    onOpenEditDialog = profileViewModel::openEditProfileDialog,
                    onCloseEditDialog = profileViewModel::closeEditProfileDialog,
                    onEditFullNameChanged = profileViewModel::onEditFullNameChanged,
                    onEditPhoneChanged = profileViewModel::onEditPhoneChanged,
                    onEditAvatarUrlChanged = profileViewModel::onEditAvatarUrlChanged,
                    onSaveProfile = profileViewModel::saveProfile,
                    onOpenChangePasswordDialog = profileViewModel::openChangePasswordDialog,
                    onCloseChangePasswordDialog = profileViewModel::closeChangePasswordDialog,
                    onOldPasswordChanged = profileViewModel::onOldPasswordChanged,
                    onNewPasswordChanged = profileViewModel::onNewPasswordChanged,
                    onConfirmNewPasswordChanged = profileViewModel::onConfirmNewPasswordChanged,
                    onSubmitChangePassword = profileViewModel::submitChangePassword,
                    onLogoutClick = profileViewModel::logout,
                    onLoggedOut = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToNotifications = { navController.navigate("notifications") }
                )
            }

            composable("notifications") {
                val notificationViewModel: NotificationViewModel = hiltViewModel()
                val uiState by notificationViewModel.uiState.collectAsState()

                NotificationScreen(
                    uiState = uiState,
                    onFilterSelected = notificationViewModel::setFilter,
                    onNotificationClick = { item ->
                        notificationViewModel.markAsRead(item)
                        NotificationNavigator.handleNotificationClick(item.targetScreen, item.targetId, navController)
                    },
                    onMarkAllAsReadClick = notificationViewModel::markAllAsRead,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
