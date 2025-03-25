package com.example.calculatorimc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signUpButton)

        // Login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication Login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, navigate to IMC Calculator
                        val intent = Intent(this, ImcCalculatorActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish() // Close login activity
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(baseContext, "Autenticação falhou: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Sign Up button click listener
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication Sign Up
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success, navigate to IMC Calculator
                        val intent = Intent(this, ImcCalculatorActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish() // Close login activity
                    } else {
                        // If sign up fails, display a message to the user
                        Toast.makeText(baseContext, "Cadastro falhou: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}