package com.example.notepassapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val loginButton: Button = findViewById(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (isValidEmail(email) && isValidPassword(password)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login successful, navigate to the main screen
                            Toast.makeText(this, "Sign Up successful", Toast.LENGTH_SHORT).show()
                            // Start the main activity or home screen here
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "Email is already registered.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Sign-up failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "Invalid Email or Password!!", Toast.LENGTH_SHORT).show()
            }
        }

        val passwordToggleIcon = findViewById<ImageView>(R.id.passwordToggleIcon)

        passwordToggleIcon.setOnClickListener {
            try {
                if (passwordEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    passwordToggleIcon.setImageResource(R.drawable.ic_eye_open)
                } else {
                    passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                    passwordToggleIcon.setImageResource(R.drawable.ic_eye)
                }
                // Keep the cursor at the end of the text after changing the transformation
                passwordEditText.setSelection(passwordEditText.text.length)
            } catch (e: Exception) {
                Log.e("PasswordToggle", "Error toggling password visibility", e)
            }
        }
    }

    fun navigateToLogIn(view: View) {
        // Create an Intent to navigate to the SignUpActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Check length requirement
        if (password.length < 6 || password.length > 14) {
            return false
        }

        // Check for at least one uppercase letter, one lowercase letter, one digit, and one special character
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        if (!hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
            return false
        }

        // Check for no more than 3 consecutive identical characters
        for (i in 0 until password.length - 3) {
            if (password[i] == password[i + 1] && password[i] == password[i + 2] && password[i] == password[i + 3]) {
                return false
            }
        }

        return true
    }
}
