package com.example.calculatorimc

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: HistoryAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var imcRecordsList: MutableList<ImcRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Inicializa as views
        recyclerView = findViewById(R.id.historyRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        progressBar = findViewById(R.id.progressBar)

        // Inicializa o Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Configura o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        imcRecordsList = mutableListOf()
        adapter = HistoryAdapter(imcRecordsList)
        recyclerView.adapter = adapter

        // Carrega os dados do Firebase
        loadHistoryData()
    }

    private fun loadHistoryData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Mostra o progresso
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.GONE

            // Referência para o nó de histórico do usuário
            val historyRef = database.getReference("users/${currentUser.uid}/imc_history")

            // Obtém todos os registros, ordenados pela data (mais recentes primeiro)
            historyRef.orderByChild("date").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Limpa a lista atual
                    imcRecordsList.clear()

                    // Processa os dados obtidos
                    if (snapshot.exists()) {
                        // Adiciona cada registro à lista
                        for (recordSnapshot in snapshot.children) {
                            val record = recordSnapshot.getValue(ImcRecord::class.java)
                            record?.let { imcRecordsList.add(0, it) } // Adiciona no início para ordem cronológica inversa
                        }

                        // Atualiza o adapter
                        adapter.updateData(imcRecordsList)

                        // Mostra o RecyclerView
                        recyclerView.visibility = View.VISIBLE
                        emptyTextView.visibility = View.GONE
                    } else {
                        // Não existem registros, mostra mensagem de vazio
                        recyclerView.visibility = View.GONE
                        emptyTextView.visibility = View.VISIBLE
                    }

                    // Esconde o progresso
                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    // Esconde o progresso e mostra mensagem de erro
                    progressBar.visibility = View.GONE
                    emptyTextView.text = "Erro ao carregar dados: ${error.message}"
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            })
        } else {
            // Usuário não está logado, mostra mensagem
            progressBar.visibility = View.GONE
            emptyTextView.text = "Usuário não autenticado"
            emptyTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }
}