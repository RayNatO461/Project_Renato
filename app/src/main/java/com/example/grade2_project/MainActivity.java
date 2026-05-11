package com.example.grade2_project;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Button btnReset, btnPrevious;

    // Counters
    private int countDown = 0;
    private int countUp = 0;
    private boolean isUpsideDown = false;
    private long lastTime = 0;

    // UI e Sons
    private TextView txtDown, txtUp;
    private MediaPlayer mpDown, mpUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar UI
        txtDown = findViewById(R.id.txtDown);
        txtUp = findViewById(R.id.txtUp);

        // Inicializar Sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Inicializar Sons (certifique-se de que os arquivos estão em res/raw)
        mpDown = MediaPlayer.create(this, R.raw.down_sound);
        mpUp = MediaPlayer.create(this, R.raw.up_sound);

        btnReset = findViewById(R.id.btnReset);
        btnPrevious = findViewById(R.id.btnPrevious);

        // Configura o que acontece ao clicar em Salvar/Resetar
        btnReset.setOnClickListener(v -> saveAndReset());

        // Configura o que acontece ao clicar em Ver Histórico
        btnPrevious.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float y = event.values[1]; // Eixo Vertical
            long currentTime = System.currentTimeMillis();

            // Intervalo de 600ms para evitar disparos múltiplos rápidos demais
            if ((currentTime - lastTime) > 600) {

                // Lógica para CABEÇA PARA BAIXO
                if (y < -7.0 && !isUpsideDown) {
                    isUpsideDown = true;
                    countDown++;
                    lastTime = currentTime;

                    playTrack(mpDown);
                    txtDown.setText("Cabeça para baixo: " + countDown);
                }

                // Lógica para VOLTAR AO NORMAL (CABEÇA PARA CIMA)
                else if (y > 7.0 && isUpsideDown) {
                    isUpsideDown = false;
                    countUp++;
                    lastTime = currentTime;

                    playTrack(mpUp);
                    txtUp.setText("Cabeça para cima: " + countUp);
                }
            }
        }
    }

    private void playTrack(MediaPlayer player) {
        if (player != null) {
            if (player.isPlaying()) player.pause();
            player.seekTo(0);
            player.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Não necessário para esta aplicação
    }

    @Override
    protected void onDestroy() {
        // 1. Primeiro salvamos os dados (se houver contagem)
        if (countDown > 0 || countUp > 0) {
            String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            Records newRecords = new Records(countDown, countUp, dataAtual + " (Auto-Save)");

            // Usamos uma Thread simples aqui
            new Thread(() -> {
                AppDatabase.getInstance(this).recordsDao().insert(newRecords);
            }).start();
        }

        // 2. IMPORTANTE: Limpar os recursos que já tínhamos antes
        if (mpDown != null) {
            mpDown.release();
            mpDown = null;
        }
        if (mpUp != null) {
            mpUp.release();
            mpUp = null;
        }

        super.onDestroy();
    }

    private void saveAndReset() {
        // Só salva se houver alguma contagem
        if (countDown > 0 || countUp > 0) {

            // Pega a data e hora atual formatada
            String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Cria o objeto com os números atuais
            Records newRecords = new Records(countDown, countUp, dataAtual);

            // Salva no banco de dados em uma Thread separada (para não travar o app)
            new Thread(() -> {
                AppDatabase.getInstance(this).recordsDao().insert(newRecords);

                // Volta para a thread principal para limpar a tela
                runOnUiThread(() -> {
                    countDown = 0;
                    countUp = 0;
                    txtDown.setText("Turned Down Counter: 0");
                    txtUp.setText("Turned Up Counter: 0");
                    Toast.makeText(this, "Counter saved in Records!", Toast.LENGTH_SHORT).show();
                });
            }).start();
        }
    }

}