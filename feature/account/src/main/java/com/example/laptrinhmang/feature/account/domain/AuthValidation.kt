package com.example.laptrinhmang.feature.account.domain

object AuthValidation {

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return emailRegex.matches(email.trim()) && email.trim().lowercase().endsWith("dau.edu.vn")
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigitOrSpecial = password.any { it.isDigit() || !it.isLetterOrDigit() }
        return hasLetter && hasDigitOrSpecial
    }

    fun isValidUserCode(userCode: String): Boolean {
        return userCode.trim().length >= 4
    }

    fun isValidOtp(otp: String): Boolean {
        return otp.trim().length == 6 && otp.trim().all { it.isDigit() }
    }
}
