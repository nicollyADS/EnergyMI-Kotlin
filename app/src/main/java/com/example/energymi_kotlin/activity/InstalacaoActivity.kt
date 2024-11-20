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
import com.example.energymi_kotlin.model.Instalacao
import com.example.energymi_kotlin.repository.InstalacaoRepository

class InstalacaoActivity : Activity(){
    private lateinit var instalacaoRepository: InstalacaoRepository
    private lateinit var instalacoes: List<Instalacao>
    private lateinit var listViewInstalacao: ListView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.instalacao)

        listViewInstalacao = findViewById(R.id.listViewInstalacao)
        sharedPreferences = getSharedPreferences("InstalacaoPrefs", Context.MODE_PRIVATE)
        instalacaoRepository = InstalacaoRepository()

        atualizarListaInstalacoes(listViewInstalacao)

        listViewInstalacao.setOnItemLongClickListener { _, _, position, _ ->
            val instalacaoSelecionada = instalacoes[position]
            abrirTelaDeEdicao(instalacaoSelecionada)
            true
        }

        listViewInstalacao.setOnItemClickListener { _, _, position, _ ->
            val instalacaoSelecionada = instalacoes[position]
            confirmarExclusao(instalacaoSelecionada)
        }

        carregarUltimosDados()
    }

    private fun carregarUltimosDados() {
        val estado = sharedPreferences.getString("estado", "")
        val cidade = sharedPreferences.getString("cidade", "")
        val bairro = sharedPreferences.getString("bairro", "")
        val rua = sharedPreferences.getString("rua", "")
        val endereco = sharedPreferences.getInt("endereco", 0)
        val observacoes = sharedPreferences.getString("observacoes", "")

        if (!estado.isNullOrEmpty() || !cidade.isNullOrEmpty() || !bairro.isNullOrEmpty()) {
            Toast.makeText(this, "Dados da última instalação editada carregados.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarListaInstalacoes(listView: ListView) {
        instalacaoRepository.buscarInstalacoes { instalacoes, erro ->
            if (erro != null) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao buscar instalações: $erro", Toast.LENGTH_SHORT).show()
                }
            } else {
                this.instalacoes = instalacoes ?: emptyList()

                val listaInstalacoesStr = this.instalacoes.map {
                    "Estado: ${it.estado}\nCidade: ${it.cidade}\nBairro: ${it.bairro}\nRua: ${it.rua}\nEndereço: ${it.endereco}\nObservações: ${it.observacoes}"
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInstalacoesStr)
                    listView.adapter = adapter
                }
            }
        }
    }

    private fun abrirTelaDeEdicao(instalacao: Instalacao) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_instalacao, null)

        val editEstado = dialogView.findViewById<EditText>(R.id.editEstado)
        val editCidade = dialogView.findViewById<EditText>(R.id.editCidade)
        val editBairro = dialogView.findViewById<EditText>(R.id.editBairro)
        val editRua = dialogView.findViewById<EditText>(R.id.editRua)
        val editEndereco = dialogView.findViewById<EditText>(R.id.editEndereco)
        val editObservacoes = dialogView.findViewById<EditText>(R.id.editObservacoes)

        editEstado.setText(instalacao.estado)
        editCidade.setText(instalacao.cidade)
        editBairro.setText(instalacao.bairro)
        editRua.setText(instalacao.rua)
        editEndereco.setText(instalacao.endereco.toString())
        editObservacoes.setText(instalacao.observacoes)

        AlertDialog.Builder(this)
            .setTitle("Editar Instalação")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                instalacao.estado = editEstado.text.toString()
                instalacao.cidade = editCidade.text.toString()
                instalacao.bairro = editBairro.text.toString()
                instalacao.rua = editRua.text.toString()
                instalacao.endereco = editEndereco.text.toString().toIntOrNull() ?: 0
                instalacao.observacoes = editObservacoes.text.toString()

                salvarDadosNoSharedPreferences(instalacao)

                instalacaoRepository.editarInstalacao(instalacao) { sucesso, erro ->
                    if (sucesso) {
                        atualizarListaInstalacoes(listViewInstalacao)
                        Toast.makeText(this, "Instalação editada com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao editar instalação: $erro", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun salvarDadosNoSharedPreferences(instalacao: Instalacao) {
        val editor = sharedPreferences.edit()
        editor.putString("estado", instalacao.estado)
        editor.putString("cidade", instalacao.cidade)
        editor.putString("bairro", instalacao.bairro)
        editor.putString("rua", instalacao.rua)
        editor.putInt("endereco", instalacao.endereco)
        editor.putString("observacoes", instalacao.observacoes)
        editor.apply()
    }

    private fun confirmarExclusao(instalacao: Instalacao) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Você realmente deseja excluir esta instalação?")
            .setPositiveButton("Sim") { _, _ ->
                instalacao.id?.let { id ->
                    instalacaoRepository.excluirInstalacao(id) { sucesso, erro ->
                        if (sucesso) {
                            atualizarListaInstalacoes(listViewInstalacao)
                            Toast.makeText(this, "Instalação excluída com sucesso", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao excluir instalação: $erro", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(this, "ID da instalação não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

}
