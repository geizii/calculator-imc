package com.example.calculatorimc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ImcCalculatorActivity : AppCompatActivity() {

    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var historyButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usa o layout correto para esta Activity
        setContentView(R.layout.activity_imc_calculator)

        // Inicializa o Firebase Auth e Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Inicializa as views vinculando-as aos IDs do layout
        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        calculateButton = findViewById(R.id.calculateButton)
        resultTextView = findViewById(R.id.resultTextView)
        logoutButton = findViewById(R.id.logoutButton)
        historyButton = findViewById(R.id.historyButton)

        // Configura o listener para o botão de calcular
        calculateButton.setOnClickListener {
            calculateIMC()
        }

        // Configura o listener para o botão de logout
        logoutButton.setOnClickListener {
            // Desloga o usuário atual
            auth.signOut()

            // Redireciona para a tela de login
            val intent = Intent(this, MainActivity::class.java)
            // Limpa a pilha de atividades para evitar voltar para a calculadora ao pressionar "voltar"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Configura o listener para o botão de histórico
        historyButton.setOnClickListener {
            // Navega para a activity de histórico
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateIMC() {
        val weightStr = weightEditText.text.toString().trim()
        val heightStr = heightEditText.text.toString().trim()

        // Verifica se os campos foram preenchidos
        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val weight = weightStr.toFloat()
            val height = heightStr.toFloat()

            // Valida se os valores são maiores que zero
            if (weight <= 0 || height <= 0) {
                Toast.makeText(this, "Peso e altura devem ser maiores que zero", Toast.LENGTH_SHORT).show()
                return
            }

            // Calcula o IMC (peso / altura²)
            val imc = weight / (height * height)

            // Exibe o resultado na tela
            showResult(imc)

            // Salva o resultado no Firebase
            saveResultToFirebase(weight, height, imc)

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResult(imc: Float) {
        // Formata o valor do IMC para uma casa decimal
        val imcFormatter = NumberFormat.getNumberInstance(Locale("pt", "BR"))
        imcFormatter.maximumFractionDigits = 1
        val imcFormatted = imcFormatter.format(imc)

        // Determina a categoria do IMC e a mensagem
        val message = when {
            imc < 18.5 -> "Seu IMC é $imcFormatted. Você está abaixo do peso."
            imc < 25 -> "Seu IMC é $imcFormatted. Você está com peso normal."
            imc < 30 -> "Seu IMC é $imcFormatted. Você está com sobrepeso."
            imc < 35 -> "Seu IMC é $imcFormatted. Você está com obesidade grau I."
            imc < 40 -> "Seu IMC é $imcFormatted. Você está com obesidade grau II."
            else -> "Seu IMC é $imcFormatted. Você está com obesidade grau III."
        }

        // Exibe a mensagem no TextView
        resultTextView.text = message
    }

    private fun saveResultToFirebase(weight: Float, height: Float, imc: Float) {
        // Verifica se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Referência para o nó do usuário atual no database
            val userRef = database.getReference("users/${currentUser.uid}/imc_history")

            // Cria um ID único para este registro
            val recordId = userRef.push().key

            if (recordId != null) {
                // Obtém a data e hora atual formatada
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val currentDate = sdf.format(Date())

                // Cria o objeto com os dados do IMC
                val imcRecord = HashMap<String, Any>()
                imcRecord["date"] = currentDate
                imcRecord["weight"] = weight
                imcRecord["height"] = height
                imcRecord["imc"] = imc
                imcRecord["category"] = getCategoryFromIMC(imc)

                // Salva no Firebase
                userRef.child(recordId).setValue(imcRecord)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Resultado salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun getCategoryFromIMC(imc: Float): String {
        return when {
            imc < 18.5 -> "Abaixo do peso"
            imc < 25 -> "Peso normal"
            imc < 30 -> "Sobrepeso"
            imc < 35 -> "Obesidade grau I"
            imc < 40 -> "Obesidade grau II"
            else -> "Obesidade grau III"
        }
    }
}