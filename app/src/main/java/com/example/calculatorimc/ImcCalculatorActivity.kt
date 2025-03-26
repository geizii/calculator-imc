package com.example.calculatorimc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class ImcCalculatorActivity : AppCompatActivity() {

    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usa o layout correto para esta Activity
        setContentView(R.layout.activity_imc_calculator)

        // Inicializa as views vinculando-as aos IDs do layout
        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        calculateButton = findViewById(R.id.calculateButton)
        resultTextView = findViewById(R.id.resultTextView)


        // Configura o listener para o botão de calcular
        calculateButton.setOnClickListener {
            calculateIMC()
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
            showResult(imc)

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
}