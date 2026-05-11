package com.example.grade2_project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        listView = findViewById(R.id.listViewRecords);

        carregarDados();
    }

    private void carregarDados() {
        new Thread(() -> {
            // Procura todos os registos no banco de dados
            List<Records> listRecords = AppDatabase.getInstance(this).recordsDao().buscarTodos();

            // Cria uma lista de Strings para exibir no ListView
            List<String> exibicao = new ArrayList<>();
            for (Records r : listRecords) {
                String linha = "Data: " + r.data +
                        "\nDown: " + r.qtdDown + " | Up: " + r.qtdUp;
                exibicao.add(linha);
            }

            // Atualiza a interface (UI) com a lista de textos
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        exibicao
                );
                listView.setAdapter(adapter);
            });
        }).start();
    }
}