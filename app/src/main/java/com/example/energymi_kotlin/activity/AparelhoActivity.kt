package com.example.energymi_kotlin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.energymi_kotlin.R
import com.example.energymi_kotlin.model.Aparelho
import com.example.energymi_kotlin.repository.AparelhoRepository

class AparelhoActivity : Activity(){
    private lateinit var aparelhoRepository: AparelhoRepository
    private lateinit var aparelhos: List<Aparelho>
    private lateinit var listViewAparelho: ListView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.aparelho)

        listViewAparelho = findViewById(R.id.listViewAparelho)
        sharedPreferences = getSharedPreferences("AparelhoPrefs", Context.MODE_PRIVATE)
        aparelhoRepository = AparelhoRepository()

        atualizarListaAparelhos(listViewAparelho)

        listViewAparelho.setOnItemLongClickListener { _, _, position, _ ->
            val aparelhoSelecionado = aparelhos[position]
            abrirTelaDeEdicao(aparelhoSelecionado)
            true
        }

        listViewAparelho.setOnItemClickListener { _, _, position, _ ->
            val aparelhoSelecionado = aparelhos[position]
            confirmarExclusao(aparelhoSelecionado)
        }

        carregarUltimosDados()
    }

    private fun carregarUltimosDados() {
        val nome = sharedPreferences.getString("nome", "")
        val tipo = sharedPreferences.getString("tipo", "")
        val watts = sharedPreferences.getInt("watts", 0)

        if (!nome.isNullOrEmpty() || !tipo.isNullOrEmpty() || watts != 0) {
            Toast.makeText(this, "Dados do último aparelho editado carregados.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarListaAparelhos(listView: ListView) {
        aparelhoRepository.buscarAparelhos { aparelhos, erro ->
            if (erro != null) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao buscar aparelhos: $erro", Toast.LENGTH_SHORT).show()
                }
            } else {
                this.aparelhos = aparelhos ?: emptyList()

                val listaAparelhosStr = this.aparelhos.map {
                    "Nome: ${it.nome}\nTipo: ${it.tipo}\nWatts: ${it.watts}"
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaAparelhosStr)
                    listView.adapter = adapter
                }
            }
        }
    }

    private fun abrirTelaDeEdicao(aparelho: Aparelho) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_aparelho, null)

        val editNome = dialogView.findViewById<EditText>(R.id.editNome)
        val editTipo = dialogView.findViewById<EditText>(R.id.editTipo)
        val editWatts = dialogView.findViewById<EditText>(R.id.editWatts)

        editNome.setText(aparelho.nome)
        editTipo.setText(aparelho.tipo)
        editWatts.setText(aparelho.watts.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Aparelho")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                aparelho.nome = editNome.text.toString()
                aparelho.tipo = editTipo.text.toString()
                aparelho.watts = editWatts.text.toString().toInt()

                salvarDadosNoSharedPreferences(aparelho)

                aparelhoRepository.editarAparelho(aparelho) { sucesso, erro ->
                    if (sucesso) {
                        atualizarListaAparelhos(listViewAparelho)
                        Toast.makeText(this, "Aparelho editado com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao editar aparelho: $erro", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun salvarDadosNoSharedPreferences(aparelho: Aparelho) {
        val editor = sharedPreferences.edit()
        editor.putString("nome", aparelho.nome)
        editor.putString("tipo", aparelho.tipo)
        editor.putInt("watts", aparelho.watts)
        editor.apply()
    }

    private fun confirmarExclusao(aparelho: Aparelho) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Você realmente deseja excluir este aparelho?")
            .setPositiveButton("Sim") { _, _ ->
                aparelho.id?.let { id ->
                    aparelhoRepository.excluirAparelho(id) { sucesso, erro ->
                        if (sucesso) {
                            atualizarListaAparelhos(listViewAparelho)
                            Toast.makeText(this, "Aparelho excluído com sucesso", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao excluir aparelho: $erro", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(this, "ID do aparelho não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }
}