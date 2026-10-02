package com.example.bitfitpart2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthActivity : AppCompatActivity() {
    companion object {
        private const val AUTH_LOG_TAG = "BitFitAuth"
    }

    private lateinit var emailEntry: EditText
    private lateinit var passwordEntry: EditText
    private lateinit var errorText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var primaryActionButton: Button
    private lateinit var toggleModeButton: Button
    private lateinit var auth: FirebaseAuth
    private var isRegisterMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        auth = FirebaseAuth.getInstance()

        emailEntry = findViewById(R.id.emailEntry)
        passwordEntry = findViewById(R.id.passwordEntry)
        errorText = findViewById(R.id.errorText)
        progressBar = findViewById(R.id.authProgressBar)
        primaryActionButton = findViewById(R.id.primaryActionButton)
        toggleModeButton = findViewById(R.id.toggleModeButton)

        updateModeUi()

        toggleModeButton.setOnClickListener {
            isRegisterMode = !isRegisterMode
            hideError()
            updateModeUi()
        }

        primaryActionButton.setOnClickListener { attemptAuth() }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            goToMainApp()
        }
    }

    private fun updateModeUi() {
        primaryActionButton.text = if (isRegisterMode) getString(R.string.register) else getString(R.string.sign_in)
        toggleModeButton.text =
            if (isRegisterMode) getString(R.string.switch_to_sign_in) else getString(R.string.switch_to_register)
    }

    private fun attemptAuth() {
        val email = emailEntry.text.toString().trim()
        val password = passwordEntry.text.toString()

        val emailError = AuthValidator.validateEmail(email)
        if (emailError != null) {
            showError(emailErrorMessage(emailError))
            return
        }

        val passwordError = AuthValidator.validatePassword(password)
        if (passwordError != null) {
            showError(passwordErrorMessage(passwordError))
            return
        }

        hideError()
        setLoading(true)

        val task = if (isRegisterMode) {
            auth.createUserWithEmailAndPassword(email, password)
        } else {
            auth.signInWithEmailAndPassword(email, password)
        }

        task.addOnCompleteListener { result ->
            setLoading(false)
            if (result.isSuccessful) {
                goToMainApp()
            } else {
                logAuthFailureIfDebug(result.exception)
                showError(firebaseErrorMessage(result.exception))
            }
        }
    }

    private fun logAuthFailureIfDebug(exception: Exception?) {
        if (!BuildConfig.DEBUG || exception == null) return
        val errorCode = (exception as? FirebaseAuthException)?.errorCode ?: "n/a"
        val action = if (isRegisterMode) "register" else "sign_in"
        Log.d(
            AUTH_LOG_TAG,
            "action=$action exceptionType=${exception::class.java.name} errorCode=$errorCode message=${exception.message}"
        )
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        primaryActionButton.isEnabled = !isLoading
        toggleModeButton.isEnabled = !isLoading
        emailEntry.isEnabled = !isLoading
        passwordEntry.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        errorText.text = ""
        errorText.visibility = View.GONE
    }

    private fun emailErrorMessage(error: AuthValidator.EmailError): String = when (error) {
        AuthValidator.EmailError.REQUIRED -> getString(R.string.error_email_required)
        AuthValidator.EmailError.INVALID_FORMAT -> getString(R.string.error_email_invalid)
    }

    private fun passwordErrorMessage(error: AuthValidator.PasswordError): String = when (error) {
        AuthValidator.PasswordError.REQUIRED -> getString(R.string.error_password_required)
        AuthValidator.PasswordError.TOO_SHORT -> getString(R.string.error_password_too_short)
    }

    private fun firebaseErrorMessage(exception: Exception?): String {
        if (exception is FirebaseNetworkException) {
            return getString(R.string.error_network)
        }
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> getString(R.string.error_email_invalid)
            "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND", "ERROR_INVALID_CREDENTIAL" ->
                getString(R.string.error_invalid_credentials)
            "ERROR_EMAIL_ALREADY_IN_USE" -> getString(R.string.error_email_in_use)
            "ERROR_WEAK_PASSWORD" -> getString(R.string.error_password_too_short)
            "ERROR_USER_DISABLED" -> getString(R.string.error_account_disabled)
            else -> getString(R.string.error_auth_generic)
        }
    }

    private fun goToMainApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
