package com.example.laptrinhmang.feature.account

import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.Role
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.feature.account.data.AuthRepository
import com.example.laptrinhmang.feature.account.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_with_empty_email_shows_error() = runTest {
        viewModel.onEmailChanged("")
        viewModel.onPasswordChanged("Password123@")
        viewModel.login()

        assertEquals("Vui lòng nhập Email trường.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun login_with_empty_password_shows_error() = runTest {
        viewModel.onEmailChanged("admin@dau.edu.vn")
        viewModel.onPasswordChanged("")
        viewModel.login()

        assertEquals("Vui lòng nhập mật khẩu.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun login_success_updates_state_with_user() = runTest {
        viewModel.onEmailChanged("admin@dau.edu.vn")
        viewModel.onPasswordChanged("Password123@")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorMessage)
        assertNotNull(viewModel.uiState.value.loggedInUser)
        assertEquals("admin@dau.edu.vn", viewModel.uiState.value.loggedInUser?.email)
        assertEquals(Role.ADMIN, viewModel.uiState.value.loggedInUser?.role)
    }

    @Test
    fun fill_mock_role_credentials_populates_correctly() {
        viewModel.fillMockRoleCredentials("ROOM_MANAGER")
        assertEquals("quanly@dau.edu.vn", viewModel.uiState.value.email)
        assertEquals("Password123@", viewModel.uiState.value.password)
    }
}

private class FakeAuthRepository : AuthRepository {
    override val currentUserFlow: Flow<User?> = flowOf(null)

    override suspend fun login(email: String, password: String): NetworkResult<User> {
        val user = User(
            id = 1,
            username = "user123",
            fullName = "Fake User",
            email = email,
            role = if (email.contains("admin")) Role.ADMIN else Role.STUDENT
        )
        return NetworkResult.Success(user)
    }

    override suspend fun logout(): NetworkResult<Unit> = NetworkResult.Success(Unit)

    override suspend fun getProfile(): NetworkResult<User> {
        return NetworkResult.Success(
            User(id = 1, username = "test", fullName = "Test User", email = "test@dau.edu.vn", role = Role.STUDENT)
        )
    }

    override suspend fun updateProfile(fullName: String, phone: String?, avatarUrl: String?): NetworkResult<User> {
        return NetworkResult.Success(
            User(id = 1, username = "test", fullName = fullName, email = "test@dau.edu.vn", role = Role.STUDENT, phone = phone, avatarUrl = avatarUrl)
        )
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): NetworkResult<Unit> = NetworkResult.Success(Unit)
}
