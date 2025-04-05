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

        // Inicializa o firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Inicializa as views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signUpButton)

        // Listener do botão de Login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validação input
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
                        // Se o login falhar, mostrar a mensagem pro usuário
                        Toast.makeText(baseContext, "Você não possui um usuário registrado. Cadastre-se e tente novamente.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Cadastro button listener
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Vaalidação Input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication Cadastro
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Cadastro feito com sucesso
                        val intent = Intent(this, ImcCalculatorActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish() // Fecha a activity
                    } else {
                        // Se o cadastro falhar
                        Toast.makeText(baseContext, "Cadastro falhou: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}